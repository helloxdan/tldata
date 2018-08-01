package org.telegram.plugins.xuser.work;

import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.ex.StopRuningException;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.listener.WebContextListener;
import com.thinkgem.jeesite.modules.tl.support.DaemonThreadFactory;

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
	static boolean run = true;
	int threadNum = Integer.parseInt(Global.getConfig("thread.work.num"));
	// 线程池
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadNum,
			new DaemonThreadFactory());
//	ScheduledExecutorService scheduledThreadPool = Executors
//			.newScheduledThreadPool(threadNum, new DaemonThreadFactory());

	public BotPool(BotManager botManager) {
		super();
		this.botManager = botManager;

		WebContextListener.addExecutorService(fixedThreadPool);
//		WebContextListener.addExecutorService(scheduledThreadPool);
	}

	public void start() {
		this.run = true;
	}

	public void stop() {
		this.run = false;
		botManager.stopReg();
	}

	public boolean isRun() {
		return this.run;
	}

	/**
	 * 从缓冲队列中取一个bot执行。
	 */
	// private void poll() {
	// BotWrapper bot = bots.poll();
	// if (bot != null) {
	// setChanged(); // 有新的实例
	// this.notifyObservers(bot); // 通知观察者有新的bot可用
	// }
	// }

	// private void pollAll() {
	// int size = bots.size();
	// for (int i = 0; i < size; i++) {
	// poll();
	// }
	//
	// }
	Map<String, Long> timeMap = Maps.newHashMap();

	public void addBot(BotWrapper botw) {
		if (!isRun())
			return;
		// check 检查bot是否正常
		XUserBot bot = botw.getBot();
		if (checkBot(bot)) {
			// bots.add(botw);
			// 尽量把时间岔开
			long delay = Long.parseLong(Global.getConfig("work.thread.delay"))
					+ RandomUtils.nextInt(1, 3);
			timeMap.put(bot.getPhone(), System.currentTimeMillis());
			
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					// 线程执行
					try {
						Thread.sleep(delay*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long diff = System.currentTimeMillis()
							- timeMap.get(bot.getPhone());
					System.err.println("线程执行时间差：" + diff / 1000);
					try {
						setChanged();
						// 有新的实例
						notifyObservers(botw); // 通知观察者有新的bot可用
					} catch (StopRuningException e) {
						System.err
								.println("程序停止执行~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						logger.error("程序停止执行，{}", e.getMessage());
						stop();
					}
				}
			});

			// scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			// @Override
			// public void run() {
			// if (!isRun())
			// return;
			// // long diff = System.currentTimeMillis()
			// // - timeMap.get(bot.getPhone());
			// // System.err.println("线程执行时间差：" + diff / 1000);
			// try {
			// setChanged(); // 有新的实例
			// notifyObservers(botw); // 通知观察者有新的bot可用
			// } catch (StopRuningException e) {
			// System.err
			// .println("程序停止执行~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			// logger.error("程序停止执行，{}", e.getMessage());
			// stop();
			// }
			// }
			// }, delay, 60 * 60 * 24 * 100, TimeUnit.SECONDS);
			// 第二个参数，表示延迟多少秒执行，可以控制线程的执行频率
			// 第三个参数，表示不要重复执行
			// FIXME 修改拉人的执行频率
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
