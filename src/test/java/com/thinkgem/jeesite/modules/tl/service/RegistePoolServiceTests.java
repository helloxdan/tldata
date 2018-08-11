/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.test.BaseServiceTests;

/**
 * 注册测试。
 * 
 * @author admin
 * @version 2018-07-24
 */
public class RegistePoolServiceTests {
	RegistePoolService service = new RegistePoolService();
	BotService botService =null;
	/**
	 * 测试多线程注册。
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRun() throws Exception {
		initRun();
		// 注册100个账号
		service.startWork(200,false);

		Thread.sleep(1000000);
	}

	public void initRun() {
		SmsCardService smsCardService = getSmsCardService();
		service.setSmsCardService(smsCardService);
		botService = getBotService();
		service.setBotService(botService);

	}

	public BotService getBotService() {
		// 覆盖方法，测试
		return new BotService() {
			@Override
			public boolean registe(String phone) {
				boolean success = RandomUtils.nextInt(1, 100) % 2 == 0;
				return success;
			}

			@Override
			public JSONObject setRegAuthCode(String phone, String code) {
				JSONObject json = new JSONObject();
				boolean success = RandomUtils.nextInt(1, 100) % 2 == 0;
				json.put("result", success);
				if (success) {
					json.put("firstName", "xu");
					json.put("lastName", "test");
				}
				return json;
			}
		};
	}

	private SmsCardService getSmsCardService() {
		// TODO Auto-generated method stub
		return new SmsCardService() {

			@Override
			public List<String> getPhoneList() {
				List<String> list = new ArrayList<String>();
				list.add("137518674" + RandomUtils.nextInt(10, 99));
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

			@Override
			public void freePhone(String phone) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void freeAllPhone() {
				// TODO Auto-generated method stub
				
			}
		};
	}

	public RegistePoolService getService() {
		return service;
	}

	public void setService(RegistePoolService service) {
		this.service = service;
	}

	public void setBotService(BotService botService) {
		this.botService = botService;
	}

}