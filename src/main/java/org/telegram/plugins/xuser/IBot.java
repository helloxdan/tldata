package org.telegram.plugins.xuser;

import org.telegram.bot.structure.LoginStatus;

import com.alibaba.fastjson.JSONObject;

public interface IBot {
	LoginStatus start(String phone, int apikey, String apihash);

	boolean stop(String phone);

	boolean setAuthCode(String phone, String code);

	boolean setAdmin(String chatId, String userId, boolean isAdmin);

	boolean importInvite(String url);

	void collectUsers(String chatId);

	void addUsers(String chatId);

	JSONObject getState();
}
