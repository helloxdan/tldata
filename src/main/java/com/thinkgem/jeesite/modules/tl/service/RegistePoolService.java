package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;

/**
 * 多线程，账号注册服务。
 * 
 * @author ThinkPad
 *
 */
@Service
@Transactional(readOnly = true)
public class RegistePoolService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BotService botService;
	@Autowired
	private SmsCardService smsCardService;
	// 累计从卡商获取的手机号数量
	private AtomicInteger phoneNum = new AtomicInteger(0);
	// 计划注册号码数，成功数量
	private int planSize = 0;// 计划获取号码数
	private AtomicInteger successSize = new AtomicInteger(0);// 成功数
	// 启动、停止标识位
	private static boolean start = false;

	int phoneThreadNum = Integer.parseInt(Global.getConfig("thread.phone.num"));
	int codeThreadNum = Integer
			.parseInt(Global.getConfig("thread.regcode.num"));
	// 获取手机号的线程池
	ExecutorService regPool = Executors.newFixedThreadPool(phoneThreadNum);
	// 查询短信验证码的线程池
	ExecutorService queryCodePool = Executors.newFixedThreadPool(codeThreadNum);
	// 查询短信验证码的线程池,定期执行，每5秒运行一次
	// ScheduledExecutorService queryCodePool = Executors
	// .newScheduledThreadPool(1);

	/**
	 * @param num
	 *            需要的成功记录数
	 */
	public void addPlanSize(int num) {
		this.planSize = this.planSize + num;
		start();
		int tryNum = num * 2;// 3倍成功记录数
		// 直接所有任务放入线程池
		for (int i = 0; i < tryNum; i++) {
			regPool.execute(new Runnable() {
				public void run() {
					// 执行任务
					getPhoneList();
				}
			});
		}
	}

	/**
	 * 开始获取手机号列表。
	 */
	public void start() {
		logger.warn("注册程序启动");
		start = true;
	}

	/**
	 * 停止获取手机号列表。
	 */
	public void stop() {
		logger.warn("注册程序停止");
		start = false;
	}

	/**
	 * 开始注册。
	 */
	private void getPhoneList() {
		if (!start)
			return;
		final int[] i = new int[] { phoneNum.get() };

		// AtomicInteger index = new AtomicInteger(phoneNum);
		// 从卡商获取电话列表
		List<String> list = getSmsCardService().getPhoneList();
		if (list != null) {
			int total = phoneNum.addAndGet(list.size());
			logger.info("本次从卡商获取手机号数={},共{}个", list.size(), total);
		}
		for (String phone : list) {
			// 检查是否查询过该账号
			if (existsPhone(phone))
				continue;

			// 执行电报的注册接口，发送验证码
			boolean success = botService.registe(phone);
			if (success) {
				// 放入线程池，等待调度
				queryCodePool.execute(new Runnable() {
					public void run() {
						// 执行查询验证码的程序
						i[0] = i[0] + 1;
						queryCode(new RegPhone(phone, i[0]));
					}

				});

			} else {
				logger.warn("{}-{}注册，发验证码失败,不列入取码队列", i[0], phone);
			}
		}
	}

	Map<String, Boolean> phoneMap = Maps.newConcurrentMap();

	private boolean existsPhone(String phone) {
		if (!phoneMap.containsKey(phone)) {
			phoneMap.put(phone, true);
			return false;
		}
		logger.warn("{},已经注册过了", phone);
		return true;
	}

	/**
	 * 查询验证码。
	 * 
	 * @param phone
	 */
	private void queryCode(RegPhone regPhone) {
		if (!start)
			return;
		int runNum = 1;
		String phone = regPhone.phone;
		do {
			logger.info("{}-{},第{}次获取验证码……", regPhone.index, phone, runNum);
			// 查询
			List<String[]> list = new ArrayList<String[]>();
			try {
				list = getSmsCardService().getPhoneCode(phone);
			} catch (Exception e) {
				if ("ignore".equals(e.getMessage())
						|| e.getMessage().contains("ignore")) {

					// 加入黑名单
					smsCardService.setForbidden(phone);
					break;
				}
			}
			if (list != null && list.size() > 0) {
				for (String[] pc : list) {
					logger.debug("{}-{}获取验证码，{}", regPhone.index, pc[0], pc[1]);
					// 注册电报账号
					sendRegCode(regPhone.index, pc[0], pc[1]);
				}

				// 获取到验证码，退出
				break;
			}
			// 累计执行次数
			runNum++;
			try {
				Thread.sleep(5000);// 等待5秒
			} catch (InterruptedException e) {
				runNum = 100;// 退出
			}
		} while (runNum <= 36);
		// 36次，3分钟

	}

	private void sendRegCode(int index, String phone, String code) {
		JSONObject json = botService.setRegAuthCode(phone, code);
		// 检查返回结果，如果成功，则清除记录；
		if (json.getBooleanValue("result")) {
			// 成功累计数
			int size = successSize.incrementAndGet();
			// 完成账号注册
			logger.info("{}-{}注册成功，{}/{}，{} {}", index, phone, size, planSize,
					json.getString("firstName"), json.getString("lastName"));

			if (size >= planSize) {
				// 到达指定数量
				logger.info("注册成功数已达到指定数量{}，停止运行", planSize);
				stop();
			}
		} else {
			// 失败，计入黑名单，调用卡商接口，标记黑名单，避免下次再获取
			logger.info("{}-{}记黑名单，", index, phone);
			smsCardService.setForbidden(phone);
		}
	}

	public SmsCardService getSmsCardService() {

		return smsCardService;
	}

	public void setSmsCardService(SmsCardService smsCardService) {
		this.smsCardService = smsCardService;
	}

	public BotService getBotService() {
		return botService;
	}

	public void setBotService(BotService botService) {
		this.botService = botService;
	}
}

class RegPhone {
	public RegPhone(String phone, int i) {
		this.phone = phone;
		this.index = i;
	}

	String phone;
	int index;
}
