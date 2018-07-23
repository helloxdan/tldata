package org.telegram.plugins.xuser.work;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.plugins.xuser.XUserBot;

/**
 * 机器池，当有新的bot，就启动任务执行操作。
 * 
 * @author ThinkPad
 *
 */
public class BotPool extends Observable {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	BotManager botManager;
	// 缓冲池
	private Queue<BotWrapper> bots = new LinkedList<BotWrapper>();
	Timer timer = new Timer();

	public BotPool(BotManager botManager) {
		super();
		this.botManager = botManager;

		startTimer();
	}

	private void startTimer() {
		// 前一次程序执行开始 后 2000ms后开始执行下一次程序
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// System.out.println("timer");
				// poll();
				pollAll();
			}

		}, 0, 2000);
	}

	/**
	 * 从缓冲队列中取一个bot执行。
	 */
	private void poll() {
		BotWrapper bot = bots.poll();
		if (bot != null) {
			setChanged(); // 有新的实例
			this.notifyObservers(bot); // 通知观察者有新的bot可用
		}
	}

	private void pollAll() {
		int size = bots.size();
		for (int i = 0; i < size; i++) {
			poll();
		}

	}

	public void addBot(BotWrapper botw) {
		// check 检查bot是否正常
		XUserBot bot = botw.getBot();
		if (checkBot(bot)) {
			bots.add(botw);
		}
	}

	private boolean checkBot(XUserBot bot) {
		boolean success = true;
		if (bot.isAuthCancel()) {
			success = false;
			// 账号已经失效
			// 删除账号
			this.botManager.deleteBot(bot, "账号失效");
		}
		return success;
	}

	/**
	 * 放入池子。
	 * 
	 * @param bot
	 * @param delay
	 *            延迟秒数，才能再次使用。
	 */
	public void put(BotWrapper botw, int delay) {
		// TODO Auto-generated method stub
		addBot(botw);
	}
}
