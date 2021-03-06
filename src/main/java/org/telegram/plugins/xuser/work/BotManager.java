package org.telegram.plugins.xuser.work;

import org.telegram.plugins.xuser.XUserBot;

public interface BotManager {
	public void addBot(String jobid, String phone);

	void destroy(XUserBot bot);

	void deleteBot(XUserBot bot, String error);

	public void stopReg();

	public void updateAccountRunResult(String phone, int usernum,int total,String status,String remark);

}
