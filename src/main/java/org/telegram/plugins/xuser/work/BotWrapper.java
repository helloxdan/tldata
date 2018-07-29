package org.telegram.plugins.xuser.work;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.RandomUtils;
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
	static AtomicInteger successTotal = new AtomicInteger(0);

	// static int successTotal = 0;

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(RandomUtils.nextInt(1, 10));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(addSuccess(1));
				}
			};
			t.setDaemon(true);
			t.start();
		}

		// 5秒后，取结果
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("最后结果：" + successTotal);
		System.out.println("最后结果：" + successTotal.get());
		// System.out.println(addSuccess(1));
		// System.out.println(addSuccess(1));
	}

	public static int getTotal() {
		// return successTotal;
		return successTotal.intValue();
		// return successTotal.addAndGet(0);
	}

	public static int addSuccess(int num) {
		int btotal = successTotal.get();
		int total = successTotal.addAndGet(num);
//		System.out.println(btotal + "+" + num + "=" + total);
		return total;
		// int btotal = successTotal;
		// successTotal = successTotal + num;
		// System.out.println(btotal + "+" + num + "=" + successTotal);
		// return successTotal;
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
