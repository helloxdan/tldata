package org.telegram.plugins.xuser.work;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.ex.ForbiddenGroupException;
import org.telegram.plugins.xuser.ex.StopRuningException;

import com.thinkgem.jeesite.modules.tl.entity.JobUser;

public class TaskExecutor implements Observer {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Logger slog = LoggerFactory.getLogger("com.telegram.success");
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
		} catch (StopRuningException e) {
			throw e;
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
		TaskData data = getTaskData(botw.getJobid(), bot.getPhone());
		if (data == null) {
			// 没有操作数据，应该直接退出
			throw new StopRuningException("群组已采集完毕");
			// 没有数据，放回bot池子
			// botpool.put(botw, 2);

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
		List<JobUser> users = null;
		try {
			users = getWorkService().collectUsers(bot, data);
		} catch (ForbiddenGroupException e) {
			logger.error("删除群组，{}，{}",data.getSrcGroupUrl(), e.getMessage());
			// 群组不允许拉人，删除群组
			taskQuery.deleteTaskGroup(bot, data);

			botpool.put(botw, 2);
			users = null;
		}
		if (users == null) {
			return;
		}

		if (users != null && users.size() == 0) {
			// 没数据
			// 把bot放回pool
			botpool.put(botw, 2);
		} else {
			// 采集和拉人，拉开一个时间差
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

			int total = 0;
			int updateNum = getWorkService().inviteUsers(bot, data, users);

			if (updateNum <= 0) {
				updateNum = 0;
				// 累计一次更新为0的操作
				// 如果超过5次，说明该账号，可能已经用满额度了
				botw.setEmptyCount(botw.getEmptyCount() + 1);

			} else {
				// 累计总完成次数
				total = BotWrapper.addSuccess(updateNum);
				botw.setEmptyCount(0);// 计数清零
				slog.info("{},本次成功{}人,已完成{}人,总{}人", bot.getPhone(), updateNum,
						botw.getUsernum() + updateNum, total);

				// 总任务完成
				if (total > BotWrapper.getPlanTotal()) {
					botManager.updateAccountRunResult(bot.getPhone(),
							botw.getUsernum(), total, "success", "成功");
				}
			}
			// 标记bot拉的人数
			botw.setUsernum(botw.getUsernum() + updateNum);

			// 如果此时拉人总数已经达到了37以上，也可以认为认为完成
			if (botw.getEmptyCount() <= 5 && botw.getUsernum() < 35) {
				// 如果拉的人数不够40，继续放入线程池
				// FIXME 如果拉的人数不够40，继续放入线程池
				botpool.put(botw, 2);
			} else {

				if (botw.getEmptyCount() > 5) {
					slog.info("{}，{}， 已完成{}/{}，账号失效，退出", bot.getJobid(),
							bot.getPhone(), botw.getUsernum(), total);
					// 如果5次，一个都没拉到，说明可能账号已经拉满人数了
					if (botw.getUsernum() == 0) {
						// FIXME 标记账号已完成任务
						botManager.updateAccountRunResult(bot.getPhone(),
								botw.getUsernum(), total, "error",
								"5次都拉不到人，可能已经拉过人");
					}
				} else {
					slog.info("{}，{}，{}/{},本号任务完成，退出", bot.getJobid(),
							bot.getPhone(), botw.getUsernum(), total);

					// FIXME 标记账号已完成任务
					botManager.updateAccountRunResult(bot.getPhone(),
							botw.getUsernum(), total, "success", "成功");
				}
				// 删除任务数据
				// getTaskQuery().deleteTaskData(bot, data);

				// bot用完，注销
				getBotManager().destroy(bot);

				getWorkService().destroy(bot, data);
				//
				botw.setBot(null);

			}
		}
	}

	public TaskData getTaskData(String jobid, String phone) {
		return getTaskQuery().getTaskData(jobid, phone);
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
