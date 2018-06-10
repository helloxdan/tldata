package org.telegram.plugins.xuser;

import java.util.List;

import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;

public interface IBot {
	LoginStatus start(String phone, int apikey, String apihash);

	boolean stop();

	boolean setAuthCode(String phone, String code);

	boolean setAdmin(int chatId, int userId, boolean isAdmin);

	boolean importInvite(String url);

	TLVector<TLAbsUser> collectUsers(int chatId,long accessHash,int offset,int limit);

	void addUsers(int chatId,long accessHash,List<JobUser> users);

	JSONObject getState();

	void getGroupInfo(int chatId, long chatAccessHash);

}
