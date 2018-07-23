package org.telegram.plugins.xuser.work;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.plugins.xuser.XUserBot;

import com.thinkgem.jeesite.modules.tl.entity.JobUser;

public class TaskExecutor implements Observer {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	BotPool botpool = null;
	TaskQuery taskQuery;
	WorkService workService;
	BotManager botManager;

	public TaskExecutor(BotManager botManager, TaskQuery taskQuery,
			WorkService workService) {
		this.botManager = botManager;
		this.taskQuery = taskQuery;
		this.workService = workService;
	}

	@Override
	public void update(Observable o, Object arg) {
		BotWrapper botw = (BotWrapper) arg;
		try {
			if (o instanceof BotPool) {
				botpool = (BotPool) o;
				// 开始执行任务
				start(botw);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("{},{},执行异常，{}", botw.getJobid(), botw.getBot()
					.getPhone(), e.getMessage());
			logger.error("", e);
			// 检查是什么问题，如果是账号问题，则提示
			// TODO

			// 如果是数据问题，丢弃本分数据
			// TODO

			// 其它问题，把bot放回池子
			botpool.put(botw, 2);

		}
	}

	/**
	 * 开始执行任务。
	 * 
	 * @param bot
	 */
	private void start(BotWrapper botw) {
		// TODO Auto-generated method stub
		XUserBot bot = botw.getBot();
		// 1.先获取一份任务数据
		TaskData data = getTaskData(botw.getJobid());
		if (data == null) {
			// 没有数据，放回bot池子
			botpool.put(botw, 2);
		} else {
			// 2.执行采集和加人操作
			work(botw, data);
		}
	}

	/**
	 * 执行任务。
	 * 
	 * @param bot
	 * @param data
	 */
	private void work(BotWrapper botw, TaskData data) {
		XUserBot bot = botw.getBot();
		// 1.采集数据
		List<JobUser> users = getWorkService().collectUsers(bot, data);
		if (users == null || users.size() == 0) {
			// 没数据
			// 把bot放回pool
			botpool.put(botw, 2);
		} else {
			getWorkService().inviteUsers(bot, data, users);
			// 标记bot拉的人数
			botw.setUsernum(botw.getUsernum() + users.size());

			// 删除任务数据
			getTaskQuery().deleteTaskData(bot, data);

			// bot用完，注销
			getBotManager().destroy(bot);

			//
			botw.setBot(null);
		}
	}

	public TaskData getTaskData(String jobid) {
		return getTaskQuery().getTaskData(jobid);
	}

	public TaskQuery getTaskQuery() {
		return taskQuery;
	}

	public void setTaskQuery(TaskQuery taskQuery) {
		this.taskQuery = taskQuery;
	}

	public WorkService getWorkService() {
		return workService;
	}

	public void setWorkService(WorkService workService) {
		this.workService = workService;
	}

	public BotManager getBotManager() {
		return botManager;
	}

	public void setBotManager(BotManager botManager) {
		this.botManager = botManager;
	}

}
