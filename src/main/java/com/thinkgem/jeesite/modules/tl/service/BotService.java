package com.thinkgem.jeesite.modules.tl.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBot;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * @author ThinkPad
 *
 */
@Service
@Transactional(readOnly = true)
public class BotService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static final int APIKEY = 202491; // your api key
	private static final String APIHASH = "9f32d44fca581599dbbe02cec25ffe58"; // your

	private Map<String, IBot> bots = new HashMap<String, IBot>();
	@Autowired
	private BotDataService botDataService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private JobService jobService;
	@Autowired
	private JobTaskService jobTaskService;
	@Autowired
	private JobUserService jobUserService;
	@Autowired
	private GroupService groupService;

	/**
	 * 程序启动初始化入口。
	 */
	@PostConstruct
	public void startInit() {
		System.out.println("Telegram bot 开始初始化……");
		if (!"true".equals(Global.getConfig("autoRun"))) {

			accountInit(null);
		}
	}

	public String accountInit(RequestData data) {
		// 1.将所有注册过的账号设置为就绪状态
		accountService.resetAccountStatus();
		// 2.将所有实例注销
		logger.info("注销所有实例");
		Set<String> sets = bots.keySet();
		for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
			String botkey = (String) iterator.next();
			IBot ins = bots.get(botkey);
			ins.stop();
			// 去除引用
			bots.remove(botkey);
		}
		// 3.管理员账号登录
		logger.info("启动管理员账号");
		XUserBot bot = new XUserBot();
		LoginStatus status = bot.start(getAdminAccount(), APIKEY, APIHASH);
		if (status == LoginStatus.ALREADYLOGGED) {
			bots.put(getAdminAccount(), bot);
			bot.setStatus(XUserBot.STATUS_OK);
		} else {
			String title = getAdminAccount() + "管理员登录失败，status" + status;
			logger.error(title);
			LogUtils.saveLog(new Log("tl", title), null);
		}
		return null;
	}

	/**
	 * 管理员账号
	 * 
	 * @return
	 */
	private String getAdminAccount() {
		// TODO Auto-generated method stub
		return "8618566104318";
	}

	/**
	 * 批量启动
	 * 
	 * @param data
	 * @return
	 */
	public String startBatch(RequestData data) {
		// TODO Auto-generated method stub
		// 取出指定数量的账号，并启动，如果启动成功，就修改账号状态为已启动
		int num = data.getNum();

		return null;
	}

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
		XUserBot b = (XUserBot) bot;
		if (!XUserBot.STATUS_WAIT.equals(b.getStatus())) {
			throw new RuntimeException(data.getPhone() + "账号实例不是在等待输入验证码状态");
		}
		boolean success = bot.setAuthCode(data.getPhone(), data.getCode());
		return success;
	}

	public boolean setAdmin(RequestData data) {
		IBot bot = getBot(data);
		bot.setAdmin(data.getChatId(), data.getUserId(), data.isAdmin());
		return false;
	}

	@Transactional(readOnly = false)
	public boolean importInvite(RequestData data) {
		IBot bot = getBot(data);
		bot.importInvite(data.getHash());
		return false;
	}

	@Transactional(readOnly = false)
	public void collectUsers(RequestData data) {
		String taskid = data.getTaskid();
		// 根据任务id，找到调用方法的参数
		JSONObject task = jobTaskService.getRpcCallInfo(taskid);

		IBot bot = getBot(data);
		TLVector<TLAbsUser> users = bot.collectUsers(
				task.getIntValue("chatid"), task.getLongValue("accesshash"),
				task.getIntValue("offsetNum"), task.getIntValue("limitNum"));
		logger.info("拉取群组用户结果：job={}，account={},size={}",
				task.getString("jobId"), task.getString("account"),
				users.size());
		
		// 将数据存储到数据库
		for (TLAbsUser tluser : users) {
			TLUser u = (TLUser) tluser;
			JobUser ju = new JobUser();
			ju.setJobId(data.getJobid());
			ju.setAccount(data.getPhone());
			ju.setFromGroup(data.getChatId() + "");
			ju.setUserid(u.getId() + "");
			ju.setUsername(u.getUserName());
			ju.setUserHash(u.getAccessHash());
			// u.getLangCode();
			// u.getFirstName();
			// u.getLastName();
			jobUserService.save(ju);
		}
	}

	/**
	 * 整理任务单的用户数据。清理重复用户，计算有效用户数。
	 * 
	 * @param data
	 */
	public void cleanJobUser(RequestData data) {
		// TODO
	}

	@Transactional(readOnly = false)
	public void addUsers(RequestData data) {
		IBot bot = getBot(data);
		bot.addUsers(data.getChatId());
	}

	public boolean stop(RequestData data) {
		IBot bot = getBot(data);
		boolean success = bot.stop();
		return success;
	}

	private IBot getBot(RequestData data) {
		IBot bot = bots.get(data.getPhone());
		if (bot == null) {
			throw new RuntimeException(data.getPhone() + "账号实例不存在");
		}
		return bot;
	}

}
