package org.telegram.plugins.xuser.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.telegram.plugins.xuser.XTelegramBot;
import org.telegram.plugins.xuser.XUserBot;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.service.BotService;

/**
 * 测试类。
 * 
 * @author ThinkPad
 *
 */
public class WorkTests {
	BotPool botPool;

	private void init() {
		BotManager botManager = new BotService();
		botPool = new BotPool(botManager);

		//
		TaskQuery taskQuery = getTaskQuery();
		WorkService workService = getWorkService();
		TaskExecutor exe = new TaskExecutor(botManager, taskQuery, workService);

		// 增加观察者
		botPool.addObserver(exe);

	}

	@Test
	public void testStartNormal() throws Exception {
		init();
		for (int i = 0; i < 100; i++) {
			BotWrapper botw = createBot(i, RandomUtils.nextInt(1, 100) % 2 == 0);
			this.botPool.addBot(botw);
			Thread.sleep(RandomUtils.nextLong(1, 5) * 1000);
		}

		Thread.sleep(1000000);

	}

	@Test
	public void testStartCancel() throws Exception {
		init();
		BotWrapper botw = createBot(1, true);
		this.botPool.addBot(botw);

		Thread.sleep(10000);

	}

	private BotWrapper createBot(int index, boolean iscancel) {
		String jobid = "test-job-" + index;
		XUserBot bot = new TestXUserBot(iscancel);
		// bot.start("8613767787663", 0, "");
		bot.setPhone("8613767787663");
		bot.setJobid(jobid);
		BotWrapper botw = new BotWrapper(jobid, bot);
		return botw;
	}

	private WorkService getWorkService() {
		return new WorkService() {

			@Override
			public void inviteUsers(XUserBot bot, TaskData data,
					List<JobUser> users) {
				// TODO Auto-generated method stub
				// System.out.println("加人……");

				if (RandomUtils.nextInt(1, 100) % 2 == 0) {
					// 随机抛出异常
					throw new RuntimeException("加人，测试异常");
				}
				for (JobUser u : users) {
					System.out.println("加人," + u.getUsername());
				}
			}

			@Override
			public List<JobUser> collectUsers(XUserBot bot, TaskData data) {
				if (RandomUtils.nextInt(1, 100) % 2 == 0) {
					// 随机抛出异常
					throw new RuntimeException("加人，测试异常");
				}
				List<JobUser> users = new ArrayList<JobUser>();
				JobUser u = new JobUser("1");
				u.setAccount("13751867473");
				u.setUsername("zhansan");
				users.add(u);
				return users;
			}
		};
	}

	private TaskQuery getTaskQuery() {
		return new TaskQuery() {

			@Override
			public TaskData getTaskData(String jobid) {
				TaskData data = new TaskData();
				data.setTaskid("task-" + RandomUtils.nextLong(1000, 100000));
				data.setDestGroupUrl("http://t.me/destUrl");
				data.setSrcGroupUrl("http://t.me/srcUrl");
				data.setOffset(0);
				data.setLimit(150);
				System.out.println("取任务数据," + JsonMapper.toJsonString(data));
				return data;
			}

			@Override
			public void deleteTaskData(XUserBot bot, TaskData data) {
				System.out.println("删除任务数据," + data.getTaskid());
			}
		};
	}
}

class TestXUserBot extends XUserBot {
	boolean iscancel = false;

	public TestXUserBot(boolean iscancel) {
		this.iscancel = iscancel;
	}

	public boolean isAuthCancel() {

		setStatus(STATUS_CANCEL);
		return iscancel;
	}
}
