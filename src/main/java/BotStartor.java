import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.XUserBot;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;
import com.thinkgem.jeesite.modules.tl.service.BotFactory;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.DefaultWorkService;
import com.thinkgem.jeesite.modules.tl.service.JobService;
import com.thinkgem.jeesite.modules.tl.service.MockSmsCardService;
import com.thinkgem.jeesite.modules.tl.service.RegisteService;
import com.thinkgem.jeesite.modules.tl.service.SmsCardService;

/**
 * 
 */

/**
 * @author ThinkPad
 *
 */
public class BotStartor {
	protected static Logger logger = LoggerFactory.getLogger(BotStartor.class);
	// 演示模式
	static boolean mock = false;

	static ApplicationContext ac;
	static BotService botService;
	static JobService jobService;
	static RegisteService registeService;
	static String cardCode;
	private static int factor = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String proxyHost = System.getProperty("proxyHost");
			String proxyPort = System.getProperty("proxyPort");
			if (StringUtils.isNotBlank(proxyHost)) {
				System.out.println("设置代理:"+proxyHost+":"+proxyPort);
				System.setProperty("socksProxyHost", proxyHost);
				System.setProperty("socksProxyPort", proxyPort);
			}

			String isdemo = System.getProperty("demo");
			String factorstr = System.getProperty("factor");
			if (StringUtils.isNotBlank(factorstr))
				factor = Integer.parseInt(factorstr);
			
			//指定dc
			String dcstr = System.getProperty("dc");
			if (StringUtils.isNotBlank(dcstr))
				XUserBot.defaultDc=Integer.parseInt(dcstr);

			// 卡商编码
			cardCode = System.getProperty("cardCode");
			if ("true".equals(isdemo)) {
				// 演示模式
				mock = true;
				cardCode = "mock";
			}
			logger.info("使用卡商编码：{}", cardCode);

			// FIXME
			initContext();
			// 取配置文件信息，得到jobid，拉人总数和采集群信息
			String cfgFileName = System.getProperty("filename");
			logger.info("读取配置文件：{}", cfgFileName);

			// 设置配置文件，用于输出采集群组的offset
			getJobService().setCfgFile(cfgFileName);

			JobData jobData = readJobData(cfgFileName);
			jobData.setJobid(DateUtils.formatDate(new Date(), "yyyyMMddhhmmss"));// 任务id

			// 开始执行
			startWork(jobData);

		} catch (Exception e) {
			logger.error("程序移除，退出运行", e);
			System.exit(0);
		}

	}

	/**
	 * 入口
	 * 
	 * @param jobData
	 */
	private static void startWork(JobData jobData) {
		List<JobGroup> list = new ArrayList<JobGroup>();
		List<String[]> urls = jobData.getFromGroupUrls();
		for (String[] url : urls) {
			JobGroup g = new JobGroup();
			g.setJobId(jobData.getJobid());
			g.setGroupId(url[0]);
			g.setGroupName(url[0]);
			g.setGroupUrl(url[0]);
			// 设置用户数量
			g.setUsernum(Integer.parseInt(url[2]));// 采集群组的总人数
			g.setOffset(Integer.parseInt(url[1]));// 位置偏移
			list.add(g);
		}
		// 设置采集群组列表
		getJobService().setJobGroupList(list);

		Job job = new Job();
		job.setId(jobData.getJobid());
		job.setGroupId(0);
		job.setGroupName(jobData.getToGroupUrl());
		job.setGroupUrl(jobData.getToGroupUrl());
		job.setUsernum(jobData.getUsernum());
		job.setAccountNum(jobData.getUsernum() / 40);// 账号数量

		System.out.println("================================================");
		System.out.println("======================开始=======================");
		System.out.println("================================================");

		getJobService().initRunData(job);
		// 启动注册程序
		getRegisteService().startWork(job.getAccountNum() * factor);
		// 调用程序，启动
		getBotService().startJob(job);
	}

	private static JobData readJobData(String cfgFileName) throws IOException {
		JobData jobData = new JobData();
		File cfgFile = new File(cfgFileName);
		List<String> lines = new ArrayList<String>();
		lines = FileUtils.readLines(cfgFile);
		// 少于三行，参数不正确
		if (lines == null || lines.size() < 3) {
			throw new RuntimeException("配置文件参数错误");
		}
		logger.info("配置文件参数：");
		// 校验
		for (String l : lines) {
			logger.info(l);
		}
		jobData.toGroupUrl = lines.get(0);
		// 拉人目标数
		jobData.usernum = Integer.parseInt(lines.get(1));

		for (int i = 2; i < lines.size(); i++) {
			jobData.addFromGroup(lines.get(i));

		}
		return jobData;
	}

	private static void initContext() {
		ac = new ClassPathXmlApplicationContext("applicationcontext.xml");
		botService = (BotService) ac.getBean("botService");
		jobService = (JobService) ac.getBean("jobService");
		registeService = (RegisteService) ac.getBean("registeService");

		// 增加模拟卡上服务
		registeService.getCardMaps().put("mock", getMockSmsCardService());
		registeService.setCardSupplier(cardCode);// 指定卡商

		// 模拟演示
		if (mock) {
			botService.setBotFactory(getBotFactory());
			logger.info("DefaultWorkService.demo={}", DefaultWorkService.demo);
			DefaultWorkService.demo = true;
		}

	}

	/**
	 * xuserbot 工厂，提供mock实例。
	 * 
	 * @return
	 */
	private static BotFactory getBotFactory() {
		return new BotFactory() {
			public XUserBot createBot() {
				return new XUserBot() {
					@Override
					public JSONObject registe(String phone, int apikey,
							String apihash) {

						JSONObject json = new JSONObject();
						// 成功
						json.put("result", "success");
						// TODO 失败
						// 模拟注册方法
						return json;
					}

					@Override
					public JSONObject setRegAuthCode(String phone, String code) {
						JSONObject json = new JSONObject();
						// 成功
						json.put("result", "true");
						json.put("firstName", "xu");
						json.put("lastName", "test");
						// TODO 失败
						// 模拟注册方法
						return json;
					}

					public boolean isAuthCancel() {
						boolean iscancel = false;
						return iscancel;
					}

					@Override
					public LoginStatus start(String phone, int apikey,
							String apihash) {
						LoginStatus status = null;
						// 成功
						status = LoginStatus.ALREADYLOGGED;
						setStatus(STATUS_OK);
						// 失败
						// setStatus(STATUS_FAIL);

						return status;
					}
				};
			}
		};
	}

	/**
	 * 模拟卡商
	 * 
	 * @return
	 */
	private static SmsCardService getMockSmsCardService() {
		// TODO Auto-generated method stub
		return new MockSmsCardService();
	}

	public static BotService getBotService() {
		return botService;
	}

	public static void setBotService(BotService botService) {
		BotStartor.botService = botService;
	}

	public static JobService getJobService() {
		return jobService;
	}

	public static void setJobService(JobService jobService) {
		BotStartor.jobService = jobService;
	}

	public static RegisteService getRegisteService() {
		return registeService;
	}

	public static void setRegisteService(RegisteService registeService) {
		BotStartor.registeService = registeService;
	}

}

class JobData {
	// 任务数据
	String jobid;
	String toGroupUrl;
	int offset; // 抽取数据的索引偏移数
	int usernum;
	List<String[]> fromGroupUrls = new ArrayList<String[]>();

	public void addFromGroup(String url) {
		if (StringUtils.isBlank(url))
			return;
		String[] urls = new String[3];
		if (url != null) {
			String[] strs = url.split(",");
			if (strs.length > 2) {
				urls[0] = strs[0];
				urls[1] = strs[1];
				urls[2] = strs[2];// 群组用户数
			} else if (strs.length > 1) {
				urls[0] = strs[0];
				urls[1] = strs[1];
				urls[2] = "10000";
			} else {
				urls[0] = strs[0];
				urls[1] = "0";
				urls[2] = "10000";
			}
		}
		fromGroupUrls.add(urls);
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public String getToGroupUrl() {
		return toGroupUrl;
	}

	public void setToGroupUrl(String toGroupUrl) {
		this.toGroupUrl = toGroupUrl;
	}

	public int getUsernum() {
		return usernum;
	}

	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public List<String[]> getFromGroupUrls() {
		return fromGroupUrls;
	}

	public void setFromGroupUrls(List<String[]> fromGroupUrls) {
		this.fromGroupUrls = fromGroupUrls;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}