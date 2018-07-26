/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBotDataService;
import org.telegram.plugins.xuser.RegKernelAuth;
import org.telegram.plugins.xuser.XTelegramBot;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.handler.ChatsHandler;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;
import org.telegram.plugins.xuser.handler.UsersHandler;
import org.telegram.plugins.xuser.support.BotConfigImpl;
import org.telegram.plugins.xuser.support.ChatUpdatesBuilderImpl;
import org.telegram.plugins.xuser.support.CustomUpdatesHandler;
import org.telegram.plugins.xuser.support.DifferenceParametersService;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.test.BaseServiceTests;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.Job;

/**
 * 工作任务Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class JobServiceTests extends BaseServiceTests {

	@Autowired
	private JobService jobService;
	@Autowired
	private BotService botService;
	// 注册服务
	@Autowired
	RegistePoolService registePoolService;

	@Before
	public void setup() {
		String username = "admin";
		String password = "admin";
		login(username, password);
	}

	private void initRunJob() {
		// 这个测试类有构造mockservice，直接拿
		SmsCardService smsCardService = getSmsCardService();
		registePoolService.setSmsCardService(smsCardService);
		// botService.setRegistePoolService(registePoolService);
		botService.setBotFactory(getBotFactory());
	}

	/**
	 * 模拟运行任务。
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRunJob() throws Exception {
		// 初始化，注入mock service
		initRunJob();

		try {
			String jobid = "20180726110703";
			botService.startJob(jobid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Thread.sleep(30000000);
	}

	/**
	 * 覆盖方法。
	 * 
	 * @return
	 */
	private BotFactory getBotFactory() {
		return new BotFactory() {
			public XUserBot createBot() {
				return new XUserBot() {
					@Override
					public JSONObject registe(String phone, int apikey,
							String apihash) {

						JSONObject json = new JSONObject();
						// 成功
						json.put("result", "success");
						// TODO 失败
						// 模拟注册方法
						return json;
					}

					@Override
					public JSONObject setRegAuthCode(String phone, String code) {
						JSONObject json = new JSONObject();
						// 成功
						json.put("result", "true");
						json.put("firstName", "xu");
						json.put("lastName", "test");
						// TODO 失败
						// 模拟注册方法
						return json;
					}
					public boolean isAuthCancel() {
						boolean iscancel = false;
						return iscancel;
					}
					@Override
					public LoginStatus start(String phone, int apikey,
							String apihash) {
						LoginStatus status = null;
						// 成功
						status = LoginStatus.ALREADYLOGGED;
						setStatus(STATUS_OK);
						// 失败
						// setStatus(STATUS_FAIL);

						return status;
					}
				};
			}
		};
	}

	private SmsCardService getSmsCardService() {
		// TODO Auto-generated method stub
		return new SmsCardService() {

			@Override
			public List<String> getPhoneList() {
				List<String> list = new ArrayList<String>();
				list.add("1375186" + RandomUtils.nextInt(1000, 9999));
				// 5秒内
				try {
					Thread.sleep(RandomUtils.nextInt(1, 5) * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return list;
			}

			@Override
			public List<String[]> getPhoneCode(String phone) {
				List<String[]> list = new ArrayList<String[]>();
				boolean success = RandomUtils.nextInt(1, 1000) % 2 == 0;
				if (success) {
					String code = "" + RandomUtils.nextInt(10000, 99999);
					list.add(new String[] { phone, code });
				} else {
					try {
						Thread.sleep(RandomUtils.nextInt(1, 5) * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return list;
			}

			@Override
			public void setForbidden(String phone) {
				System.out.println("----------------拉黑---------");
			}
		};
	}

	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	public BotService getBotService() {
		return botService;
	}

	public void setBotService(BotService botService) {
		this.botService = botService;
	}

	public RegistePoolService getRegistePoolService() {
		return registePoolService;
	}

	public void setRegistePoolService(RegistePoolService registePoolService) {
		this.registePoolService = registePoolService;
	}

}