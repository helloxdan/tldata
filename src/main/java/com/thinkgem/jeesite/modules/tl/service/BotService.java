package com.thinkgem.jeesite.modules.tl.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBot;
import org.telegram.plugins.xuser.XUserBot;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * @author ThinkPad
 *
 */
@Service
@Transactional(readOnly = true)
public class BotService {
	private static final int APIKEY = 202491; // your api key
	private static final String APIHASH = "9f32d44fca581599dbbe02cec25ffe58"; // your

	private Map<String, IBot> bots = new HashMap<String, IBot>();

	/**
	 * @param data
	 */
	public LoginStatus start(RequestData data) {
		IBot bot = bots.get(data.getPhone());
		if (bot == null) {
			bot = new XUserBot();
			bots.put(data.getPhone(), bot);
		}
		LoginStatus status = bot.start(data.getPhone(), APIKEY, APIHASH);
		return status;
	}

	public JSONObject getState(RequestData data) {
		IBot bot = getBot(data);
		JSONObject status = bot.getState();
		return status;
	}

	public boolean setAuthCode(RequestData data) {
		IBot bot = getBot(data);
		boolean success = bot.setAuthCode(data.getPhone(), data.getCode());
		return success;
	}

	public boolean setAdmin(RequestData data) {
		IBot bot = getBot(data);
		bot.setAdmin(data.getChatId(), data.getUserId(), data.isAdmin());
		return false;
	}

	public boolean importInvite(RequestData data) {
		IBot bot = getBot(data);
		bot.importInvite(data.getUrl());
		return false;
	}

	public void collectUsers(RequestData data) {
		IBot bot = getBot(data);
		bot.collectUsers(data.getChatId());
	}

	public void addUsers(RequestData data) {
		IBot bot = getBot(data);
		bot.addUsers(data.getChatId());
	}

	public boolean stop(RequestData data) {
		IBot bot = getBot(data);
		return false;
	}

	private IBot getBot(RequestData data) {
		IBot bot = bots.get(data.getPhone());
		if (bot == null) {
			throw new RuntimeException(data.getPhone() + "账号不存在");
		}
		return bot;
	}
}
