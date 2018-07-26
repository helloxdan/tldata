package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

/**
 * 账号注册服务。
 * 
 * @author ThinkPad
 *
 */
@Service
@Transactional(readOnly = true)
public class RegisteService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BotService botService;
	@Autowired
	private SmsCardService smsCardService;
	// 累计从卡商获取的手机号数量
	private int phoneNum = 0;
	// 计划注册号码数，成功数量
	private int planSize = 0;// 计划获取号码数

	private Map<String, String> phoneMaps = new HashMap<String, String>();
	// 记录待取验证码的手机号
	private Map<String, Long> codeMaps = new HashMap<String, Long>();
	// 手机队列
	private Queue<String> phoneQueue = new LinkedList<String>();
	private Queue<String[]> codeQueue = new LinkedList<String[]>();
	// 启动自动获取手机账号的功能
	private boolean start = false;

	// 线程池
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(31);

	public int getPlanSize() {
		return planSize;
	}

	public void addPlanSize(int num) {
		this.planSize = this.planSize + num;

		// 直接所有任务放入线程池
		for (int i = 0; i < num; i++) {
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					 //执行任务
					
				}
			});
		}

		start();
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

	public SmsCardService getSmsCardService() {

		return smsCardService;
	}

	public void setSmsCardService(SmsCardService smsCardService) {
		this.smsCardService = smsCardService;
	}

	public void addPhone(String phone) {
		phoneQueue.add(phone);
	}

	public void addPhoneCode(String phone, String code) {
		codeQueue.add(new String[] { phone, code });
	}

	/**
	 * 定时调度，获取手机号列表。
	 */
	public void getPhoneList() {
		if (!start)
			return;
		if (!phoneQueue.isEmpty())
			return;

		// FIXME 获取指定数量的账号即停止
		if (phoneMaps.keySet().size() > planSize) {
			logger.info("获取手机号达到{}个，停止运行。待观察效果后，再行定夺", planSize);
			stop();
			return;
		}

		List<String> list = getSmsCardService().getPhoneList();
		if (list != null) {
			phoneNum = phoneNum + list.size();
			logger.info("本次从卡商获取手机号数={},共{}个", list.size(), phoneNum);
		}
		for (String phone : list) {
			//
			phoneQueue.add(phone);
		}
	}

	/**
	 * 定时调度，获取手机验证码列表。
	 */
	public void getPhoneCodeList() {
		// if (!start)
		// return;
		Set<String> sets = codeMaps.keySet();
		if (sets.size() > 0)
			logger.info("共{}个手机号待收验证码", codeMaps.size());
		for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
			String phone = (String) iterator.next();

			Long cacheTime = codeMaps.get(phone);
			long len = System.currentTimeMillis() - cacheTime;
			if (len > 1000 * 60 * 3) {
				// 超过3分钟，就不再查询了
				iterator.remove();
				logger.warn("{},取验证码时间超过3分钟，超时，取消操作", phone);
				continue;
			}

			List<String[]> list = new ArrayList<String[]>();
			try {
				list = getSmsCardService().getPhoneCode(phone);
			} catch (Exception e) {
				if ("ignore".equals(e.getMessage())
						|| e.getMessage().contains("ignore")) {
					iterator.remove();
					// 加入黑名单
					smsCardService.setForbidden(phone);
					continue;
				}
			}

			if (list != null && list.size() > 0) {
				logger.debug("从卡商获取手机验证码记录数={}", list.size());
			}
			for (String[] pc : list) {
				codeQueue.add(pc);
				// 已经获取到验证码，移除记录
				iterator.remove();
			}
		}
	}

	/**
	 * 自动注册
	 */
	@Transactional(readOnly = false)
	public void autoRegiste() {
		// 定时调度，10秒一次
		// 检查队列中是否存在记录，有就发送验证码
		String phone = phoneQueue.poll();
		if (phone != null) {
			if (phoneMaps.containsKey(phone)) {
				logger.info("{}账号已经发送过验证码", phone);
			} else {
				// FIXME
				boolean success = botService.registe(phone);
				// boolean success = false;
				// 存入缓存。
				phoneMaps.put(phone, phone);
				if (success) {
					codeMaps.put(phone, System.currentTimeMillis());
				} else {
					logger.warn("发验证码失败,不列入取码队列");
				}
			}
		}
	}

	/**
	 * 注册单个账号。
	 * 
	 * @param phone
	 */
	@Transactional(readOnly = false)
	public void autoSentCode() {
		// 定时调度，5秒一次
		// 检查是否收到验证码，有验证码，发送验证到telegram
		String[] kv = codeQueue.poll();
		if (kv != null) {
			if (!phoneMaps.containsKey(kv[0])) {
				logger.warn("{}账号没有发送短信验证码的记录", kv[0]);
				return;
			}

			JSONObject json = botService.setRegAuthCode(kv[0], kv[1]);
			// FIXME
			// JSONObject json = new JSONObject();
			// 检查返回结果，如果成功，则清除记录；
			if (json.getBooleanValue("result")) {
				// 完成账号注册
				logger.info("账号{}注册成功，{} {}", kv[0],
						json.getString("firstName"), json.getString("lastName"));

				// 成功之后，移除记录，不再重复发送验证码
				codeMaps.remove(kv[0]);
			} else {
				// 失败，计入黑名单，调用卡商接口，标记黑名单，避免下次再获取
				logger.info("调用卡商接口，标记黑名单，{}", kv[0]);
				smsCardService.setForbidden(kv[0]);

				// 删除文件
				// TODO
			}
		}
	}
}
