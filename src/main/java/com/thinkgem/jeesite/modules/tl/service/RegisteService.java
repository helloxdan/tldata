package com.thinkgem.jeesite.modules.tl.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

	private Map<String, String> phoneMaps = new HashMap<String, String>();
	private Map<String, String> codeMaps = new HashMap<String, String>();
	// 手机队列
	private Queue<String> phoneQueue = new LinkedList<String>();
	private Queue<String[]> codeQueue = new LinkedList<String[]>();

	// 启动自动获取手机账号的功能
	private boolean start = true;

	/**
	 * 开始获取手机号列表。
	 */
	public void start() {
		start = true;
	}

	/**
	 * 停止获取手机号列表。
	 */
	public void stop() {
		start = true;
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

		List<String> list = getSmsCardService().getPhoneList();
		if (list != null) {
			logger.info("从卡商获取手机号记录数={}", list.size());
		}
		for (String phone : list) {
			phoneQueue.add(phone);
		}
	}

	/**
	 * 定时调度，获取手机验证码列表。
	 */
	public void getPhoneCodeList() {
		if (!start)
			return;

		List<String[]> list = getSmsCardService().getPhoneCodeList();
		if (list != null) {
			logger.info("从卡商获取手机验证码记录数={}", list.size());
		}
		for (String[] pc : list) {
			codeQueue.add(pc);
		}
	}

	/**
	 * 自动注册
	 */
	public void autoRegiste() {
		// 定时调度，10秒一次
		// 检查队列中是否存在记录，有就发送验证码
		String phone = phoneQueue.poll();
		if (phone != null) {
			if (phoneMaps.containsKey(phone)) {
				logger.info("{}账号已经发送过验证码", phone);
			} else {
				// botService.registe(phone);
				// 存入缓存。
				phoneMaps.put(phone, phone);
			}
		}
	}

	/**
	 * 注册单个账号。
	 * 
	 * @param phone
	 */
	public void autoSentCode() {
		// 定时调度，5秒一次
		// 检查是否收到验证码，有验证码，发送验证到telegram
		String[] kv = codeQueue.poll();
		if (kv != null) {
			if (!phoneMaps.containsKey(kv[0])) {
				logger.warn("{}账号没有发送短信验证码的记录", kv[0]);
				return;
			}
			if (codeMaps.containsKey(kv[0])) {
				logger.warn("{}账号已经发送短信验证码，不能重复发送", kv[0]);
				return;
			}
			codeMaps.put(kv[0], kv[0]);

			// JSONObject json = botService.setRegAuthCode(kv[0], kv[1]);
			JSONObject json = new JSONObject();
			// 检查返回结果，如果成功，则清除记录；
			if (json.getBooleanValue("result")) {
				// 完成账号注册
				logger.info("账号{}注册成功，{} {}", kv[0],
						json.getString("firstName"), json.getString("lastName"));
			} else {
				// 失败，计入黑名单，调用卡商接口，标记黑名单，避免下次再获取
				logger.info("调用卡商接口，标记黑名单，{}", kv[0]);
				smsCardService.setForbidden(kv[0]);
			}
		}
	}
}
