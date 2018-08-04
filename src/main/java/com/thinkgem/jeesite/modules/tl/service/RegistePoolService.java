package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.listener.WebContextListener;
import com.thinkgem.jeesite.modules.tl.support.DaemonThreadFactory;

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

	private SmsCardService smsCardService;
	// 卡商代码
	private String cardSupplier;
	private Map<String, SmsCardService> cardMaps = Maps.newHashMap();

	// 累计从卡商获取的手机号数量
	private AtomicInteger phoneNum = new AtomicInteger(0);
	// 计划注册号码数，成功数量
	private int planSize = 0;// 计划获取号码数
	private AtomicInteger successSize = new AtomicInteger(0);// 成功数
	// 启动、停止标识位
	public static boolean start = false;
	// 注册成功后，是否自动运行工单
	private boolean autoRunWork = false;
	private double phoneNumFactor = Double.parseDouble(Global
			.getConfig("reg.phonenum.factor"));
	int phoneThreadNum = 1;// Integer.parseInt(Global.getConfig("thread.phone.num"));
	int codeThreadNum = Integer
			.parseInt(Global.getConfig("thread.regcode.num"));
	long period = Long.parseLong(Global.getConfig("reg.getphone.period"));
	// 获取手机号的线程池
	ExecutorService regPool = Executors.newFixedThreadPool(phoneThreadNum,
			new DaemonThreadFactory());
	ScheduledExecutorService regScheduledThreadPool = Executors
			.newScheduledThreadPool(1, new DaemonThreadFactory());
	// 查询短信验证码的线程池
	ExecutorService queryCodePool = Executors.newFixedThreadPool(codeThreadNum,
			new DaemonThreadFactory());

	// 查询短信验证码的线程池,定期执行，每5秒运行一次
	// ScheduledExecutorService queryCodePool = Executors
	// .newScheduledThreadPool(1);
	public RegistePoolService() {
//		WebContextListener.addExecutorService(regPool);
//		WebContextListener.addExecutorService(queryCodePool);
	}

	/**
	 * @param num
	 *            需要的成功记录数
	 * @param autoRunWork
	 *            注册成功之后，改账号是否自动执行任务
	 * 
	 */
	public void startWork(int num, boolean autoRunWork) {
		this.autoRunWork = autoRunWork;
		if (num > 0) {
			logger.error("注册程序启动~~~~~~~~~~~~~~~~~~~~");
			// addPlanSize(num, false);
			this.planSize = (int) (num * phoneNumFactor);
			this.successSize.set(0);
			;
		}
		start();

		regScheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				//
				if (start && planSize > 0) {
					getPhoneList();
					planSize--;
				}
			}
		}, 1, period, TimeUnit.SECONDS);
		// 每三秒执行一次
	}

	public void returnPool(int num, boolean delay) {
		if (!start)
			return;
		// addPlanSize(num, delay);
		this.planSize = this.planSize + num;
	}

	@Deprecated
	public void addPlanSize(int num, boolean delay) {
		this.planSize = this.planSize + num;

		start();

		// 所需账号数，再乘以一个倍数，很多时候，拉人失败
		int tryNum = (int) (num * phoneNumFactor);// 3倍成功记录数
		// 直接所有任务放入线程池
		for (int i = 0; i < tryNum; i++) {
			int delayTime = (i + 1) * 3000;
			i++;
			regPool.execute(new Runnable() {
				public void run() {
					// 每个隔开2-3秒,连续执行太快，引发接口报警
					try {
						Thread.sleep(delayTime);
					} catch (InterruptedException e) {

					}
					/*
					 * if (delay) { try { Thread.sleep(5000); } catch
					 * (InterruptedException e) {
					 * 
					 * } }
					 */
					// 执行任务
					getPhoneList();

				}
			});
		}
	}

	public void addPlanSizeBak(int num, boolean delay) {
		this.planSize = this.planSize + num;

		start();

		// 所需账号数，再乘以一个倍数，很多时候，拉人失败
		int tryNum = (int) (num * phoneNumFactor);// 3倍成功记录数
		// 直接所有任务放入线程池
		for (int i = 0; i < tryNum; i++) {
			int delayTime = (i + 1) * 3000;
			i++;
			regPool.execute(new Runnable() {
				public void run() {
					// 每个隔开2-3秒,连续执行太快，引发接口报警
					try {
						Thread.sleep(delayTime);
					} catch (InterruptedException e) {

					}
					/*
					 * if (delay) { try { Thread.sleep(5000); } catch
					 * (InterruptedException e) {
					 * 
					 * } }
					 */
					// 执行任务
					getPhoneList();

				}
			});
		}
	}

	/**
	 * 注册成功后是否自动进入工作模式，false-只是注册，不做其它操作
	 * 
	 * @param auto
	 */
	public void setAutoRunWork(boolean auto) {
		this.autoRunWork = auto;
	}

	/**
	 * 开始获取手机号列表。
	 */
	public void start() {
		start = true;
	}

	public boolean isStart() {
		return start;
	}

	/**
	 * 停止获取手机号列表。
	 */
	public void stop() {
		logger.error("注册程序停止~~~~~~~~~~~~~~~~~~~");
		start = false;
	}

	/**
	 * 开始注册。
	 */
	private void getPhoneList() {
		if (!start)
			return;
		final int[] i = new int[] { phoneNum.get() };
		String phone1 = null;
		try {
			// AtomicInteger index = new AtomicInteger(phoneNum);
			// 从卡商获取电话列表
			List<String> list = getSmsCardService().getPhoneList();
			if (list != null) {
				int total = phoneNum.addAndGet(list.size());
				logger.info("取手机号数={},共{}个", list.size(), total);
			}
			for (String phone : list) {
				phone1 = phone;
				// 检查是否查询过该账号
				if (existsPhone(phone))
					continue;

				// 执行电报的注册接口，发送验证码
				// 控制频率，不能执行太密
				boolean success = botService.registe(phone);
				if (success) {
					// 放入线程池，等待调度
					queryCodePool.execute(new Runnable() {
						public void run() {
							if (!start)
								return;
							// 执行查询验证码的程序
							i[0] = i[0] + 1;
							queryCode(new RegPhone(phone, i[0]));
						}

					});

				} else {
					logger.warn("{}-{}注册，发验证码失败,不列入取码队列", i[0], phone);
					// addPlanSize(1, true);
					getSmsCardService().freePhone(phone);
					returnPool(1, true);
				}
			}
		} catch (Exception e) {
			if (e.getMessage() != null
					&& (e.getMessage().contains("余额不足") || e.getMessage()
							.contains("登录异常"))) {
				stop();
			}
			if (phone1 != null)
				getSmsCardService().freePhone(phone1);
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
					getSmsCardService().setForbidden(phone);
				}
				getSmsCardService().freePhone(phone);
				// addPlanSize(1, true);
				returnPool(1, true);
				break;
			}
			if (list != null && list.size() > 0) {
				for (String[] pc : list) {
					logger.debug("{}-{}获取验证码，{}", regPhone.index, pc[0], pc[1]);
					// 注册电报账号
					try {
						sendRegCode(regPhone.index, pc[0], pc[1]);
					} catch (Exception e) {
						logger.error("用注册码注册电报失败，{}", e.getMessage());
						// addPlanSize(1, true);
						returnPool(1, true);
						getSmsCardService().freePhone(phone);
						break;
					}
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
		JSONObject json = botService.setRegAuthCode(phone, code, autoRunWork);
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
			getSmsCardService().setForbidden(phone);
		}
	}

	public SmsCardService getSmsCardService() {
		if (StringUtils.isBlank(getCardSupplier()))
			throw new RuntimeException("没有指定卡商代码");

		smsCardService = cardMaps.get(getCardSupplier());
		if (smsCardService == null)
			throw new RuntimeException("卡商代码" + getCardSupplier() + "，没有对应实现！");
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

	public int getPlanSize() {
		return planSize;
	}

	public AtomicInteger getSuccessSize() {
		return successSize;
	}

	public String getCardSupplier() {
		return cardSupplier;
	}

	public void setCardSupplier(String cardSupplier) {
		this.cardSupplier = cardSupplier;
	}

	public Map<String, SmsCardService> getCardMaps() {
		return cardMaps;
	}

	public void setCardMaps(Map<String, SmsCardService> cardMaps) {
		this.cardMaps = cardMaps;
	}

}

 
