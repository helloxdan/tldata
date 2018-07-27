package org.telegram.plugins.xuser.work;

import java.util.concurrent.atomic.AtomicInteger;

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
	int emptyCount = 0;
	// 计划完成数
	static int planTotal = 0;
//	static AtomicInteger successTotal = new AtomicInteger(0);
	static int successTotal=0;

	
	
	public static void main(String[] args) {
		System.out.println(addSuccess(1));
		System.out.println(addSuccess(1));
		System.out.println(addSuccess(1));
	}
	public static int addSuccess(int num) {
//		return successTotal.addAndGet(num);
		successTotal=successTotal+num;
		return successTotal;
	}

	public static void setPlanTotal(int num) {
		planTotal = num;
	}

	public static int getPlanTotal() {
		return planTotal;
	}

	public BotWrapper(String jobid, XUserBot bot) {
		super();
		this.jobid = jobid;
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
