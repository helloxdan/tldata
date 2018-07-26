package org.telegram.plugins.xuser.work;

import org.telegram.plugins.xuser.XUserBot;

/**
 * 执行bot的封装。
 * 
 * @author ThinkPad
 *
 */
public class BotWrapper {
	String jobid;
	XUserBot bot;
	int usernum = 0;
	int emptyCount=0;

	public BotWrapper(String jobid,XUserBot bot) {
		super();
		this.jobid=jobid;
		this.bot = bot;
	}

	public XUserBot getBot() {
		return bot;
	}

	public void setBot(XUserBot bot) {
		this.bot = bot;
	}

	public int getUsernum() {
		return usernum;
	}

	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public int getEmptyCount() {
		return emptyCount;
	}

	public void setEmptyCount(int emptyCount) {
		this.emptyCount = emptyCount;
	}

}
