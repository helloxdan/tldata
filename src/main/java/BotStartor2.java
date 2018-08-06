import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.XUserBot;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;
import com.thinkgem.jeesite.modules.tl.service.BotFactory;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.DefaultWorkService;
import com.thinkgem.jeesite.modules.tl.service.JobService;
import com.thinkgem.jeesite.modules.tl.service.MockSmsCardService;
import com.thinkgem.jeesite.modules.tl.service.RegisteService;
import com.thinkgem.jeesite.modules.tl.service.SmsCardService;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * 
 */

/**
 * @author ThinkPad
 *
 */
public class BotStartor2 {
	protected static Logger logger = LoggerFactory.getLogger(BotStartor2.class);
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
			String isdemo = System.getProperty("demo");

			if ("true".equals(isdemo)) {
				// 演示模式
				mock = true;
			}
			// FIXME
			initContext();
			System.setProperty("socksProxyHost","127.0.0.1");
			System.setProperty("socksProxyPort","1080");
			System.out.println(System.getProperty("https.proxyHost"));
			System.out.println(System.getProperty("https.proxyPort"));
//			System.setProperty("https.proxyHost","127.0.0.1");
//			System.setProperty("https.proxyPort","1080");
			
			RequestData data = new RequestData();
			String phone = System.getProperty("phone");
			data.setPhone(phone);
			 getBotService().start(data);

//			openUrl();

		} catch (Exception e) {
			logger.error("程序移除，退出运行", e);
			System.exit(0);
		}

	}

	private static void openUrl() {
		try {
			
			// 建立连接
			URL url = new URL("https://www.google.com");
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();
			httpUrlConn.setDoInput(true);
			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			// 获取输入流
			InputStream input = httpUrlConn.getInputStream();
			// 将字节输入流转换为字符输入流
			InputStreamReader read = new InputStreamReader(input, "utf-8");
			// 为字符输入流添加缓冲
			BufferedReader br = new BufferedReader(read);
			// 读取返回结果
			String data = br.readLine();
			while (data != null) {
				System.out.println(data);
				data = br.readLine();
			}
			// 释放资源
			br.close();
			read.close();
			input.close();
			httpUrlConn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		BotStartor2.botService = botService;
	}

	public static JobService getJobService() {
		return jobService;
	}

	public static void setJobService(JobService jobService) {
		BotStartor2.jobService = jobService;
	}

	public static RegisteService getRegisteService() {
		return registeService;
	}

	public static void setRegisteService(RegisteService registeService) {
		BotStartor2.registeService = registeService;
	}

}
