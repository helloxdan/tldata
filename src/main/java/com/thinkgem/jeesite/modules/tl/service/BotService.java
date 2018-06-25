package com.thinkgem.jeesite.modules.tl.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBot;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.entity.Chat;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * @author ThinkPad
 *
 */
@Service
@EnableScheduling
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
	@Autowired
	private ChatService chatService;
public BotService() {
}
	/**
	 * 检查账号和群组的关系。如果发现有账号没有加入某个群组，则自动加入。
	 */
	 @Scheduled(cron = "0/10 * * * * ?")
	@Transactional(readOnly = false)
	public void scheduleUpdateGroupInfo() {
		logger.info("定时调度，更新群组的link和用户数量……");

		// TODO
		Group group = new Group();
		List<Group> list = groupService.findListWithoutUrl(group);
		int num = 0;
		for (Group g : list) {
			// 暂时只执行两次，检查api接口是否支持连续执行
			if (num == 2)
				break;
			updateGroupInfo(g);
			num++;
		}
	}

	@Transactional(readOnly = false)
	public void updateGroupInfo(Group g) {
		// 通过管理员账号获取信息
		try {
			IBot bot = bots.get(getAdminAccount());
			if (bot == null) {
				throw new RuntimeException(getAdminAccount() + "账号实例不存在");
			}
			JSONObject json = bot.getGroupInfo(Integer.parseInt(g.getId()),
					g.getAccesshash(), true);
			String link = json.getString("link");
			Integer usernum = json.getInteger("usernum");
			if (StringUtils.isNotBlank(link)) {
				Group group = groupService.get(g.getId());
				group.setUrl(link);
				group.setUsernum(usernum);

				groupService.save(group);
			} else {
				logger.warn("通过账号{}无法获取群组【{}】的link", getAdminAccount(),
						g.getName());
			}

		} catch (Exception e) {
			logger.error("更新群组的link地址异常", e);
		}
	}

	// @Scheduled(cron = "0 0/5 * * * ?")
	@Transactional(readOnly = false)
	public void scheduleJoinGroup() {
		logger.info("定时调度，账号自动加入有效群组……");

		// TODO
	}

	/**
	 * 定时抽取用户信息。
	 */
	// @Scheduled(cron = "0/5 * * * * ?")
	@Transactional(readOnly = false)
	public void scheduleFetchUser() {
		logger.info("定时调度，从群组抽取用户数据……");

		// TODO
	}

	/**
	 * 程序启动初始化入口。
	 */
	@PostConstruct
	@Transactional(readOnly = false)
	public void startInit() {
		System.out.println("Telegram bot 开始初始化……");
		if ("true".equals(Global.getConfig("autoRun"))) {

			accountInit(null);
		}
	}

	@Transactional(readOnly = false)
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
		bot.setBotDataService(botDataService);
		LoginStatus status = bot.start(getAdminAccount(), APIKEY, APIHASH);
		if (status == LoginStatus.ALREADYLOGGED) {
			bots.put(getAdminAccount(), bot);
			bot.setStatus(XUserBot.STATUS_OK);

			// 更改帐号状态为run
			updateAccountState(getAdminAccount(), Account.STATUS_RUN);
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
	@Transactional(readOnly = false)
	public String startBatch(RequestData data) {
		// 取出指定数量的账号，并启动，如果启动成功，就修改账号状态为已启动
		int num = data.getNum();
		Account account = new Account();
		account.setStatus(Account.STATUS_READY);
		List<Account> list = accountService.findList(account);
		logger.info("共有准备就绪账号数{},需要启动数量{}", list.size(), num);
		if (list.size() < num)
			num = list.size();
		for (int i = 0; i < num; i++) {
			Account acc = list.get(i);
			data.setPhone(acc.getId());

			start(data);
		}

		return null;
	}

	/**
	 * @param data
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public LoginStatus start(RequestData data) {
		XUserBot bot = (XUserBot) bots.get(data.getPhone());
		if (bot == null) {
			bot = new XUserBot();
			bot.setBotDataService(botDataService);
			bots.put(data.getPhone(), bot);
		} else {
			// 已经登陆过l
			if (XUserBot.STATUS_OK.equals(bot.getStatus())) {
				return LoginStatus.ALREADYLOGGED;
			}
		}
		LoginStatus status = bot.start(data.getPhone(), APIKEY, APIHASH);
		logger.info("{} start status={}", data.getPhone(), status);
		if (status == LoginStatus.ALREADYLOGGED) {
			// bots.put(getAdminAccount(), bot);
			bot.setStatus(XUserBot.STATUS_OK);
			// 更改帐号状态为run
			updateAccountState(data.getPhone(), Account.STATUS_RUN);
		} else {
			String title = data.getPhone() + "登录失败，status" + status;
			logger.error(title);
			LogUtils.saveLog(new Log("tl", title), null);
		}
		return status;
	}

	private void updateAccountState(String phone, String status) {
		Account acc = accountService.get(phone);
		acc.setStatus(status);
		accountService.save(acc);
	}

	@Transactional(readOnly = false)
	public JSONObject getState(RequestData data) {
		IBot bot = getBot(data);
		JSONObject status = bot.getState();
		return status;
	}

	@Transactional(readOnly = false)
	public boolean setAuthCode(RequestData data) {
		IBot bot = getBot(data);
		XUserBot b = (XUserBot) bot;
		if (!XUserBot.STATUS_WAIT.equals(b.getStatus())) {
			throw new RuntimeException(data.getPhone() + "账号实例不是在等待输入验证码状态");
		}
		boolean success = bot.setAuthCode(data.getPhone(), data.getCode());
		return success;
	}

	@Transactional(readOnly = false)
	public boolean setAdmin(RequestData data) {
		IBot bot = getBot(data);
		bot.setAdmin(data.getChatId(), data.getUserId(), data.isAdmin());
		return false;
	}

	@Transactional(readOnly = false)
	public JSONObject importInvite(RequestData data) {
		IBot bot = getBot(data);
		return bot.importInvite(data.getUrl());
	}

	@Transactional(readOnly = false)
	public void collectUsers(RequestData data) {
		String taskid = data.getTaskid();
		if (StringUtils.isNotBlank(taskid)) {
			collectUsersOfTask(data, taskid);
		} else {
			// 将工作任务所有task，执行抽取用户任务
			String jobid = data.getJobid();
			JobTask jobtask = new JobTask();
			jobtask.setJobId(jobid);
			jobtask.setStatus(JobTask.STATUS_NONE);//
			List<JobTask> list = jobTaskService.findList(jobtask);
			logger.info("任务{}共有调度抽人任务数{}", jobid, list.size());
			try {
				// 遍历执行所有记录
				for (JobTask jt : list) {
					collectUsersOfTask(data, jt.getId());

					Thread.sleep(1000L);// 停留1s
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void collectUsersOfTask(RequestData data, String taskid) {
		// 根据任务id，找到调用方法的参数
		JSONObject task = jobTaskService.getRpcCallInfo(taskid);
		data.setPhone(task.getString("account"));
		data.setUrl(task.getString("groupUrl"));
		data.setChatAccessHash(task.getLongValue("accessHash"));
		data.setChatId(task.getIntValue("chatid"));

		IBot bot = getBot(data);
		// if (task.getInteger("chatid") == null) {
		// throw new RuntimeException("taskid="+taskid+"的用户还没加入来源群组");
		// logger.warn("taskid={}的用户{}还没加入来源群组", taskid, data.getPhone());
		logger.info("{}加入群组{}", data.getPhone(), data.getUrl());
		JSONObject json = bot.importInvite(data.getUrl());

		// 来源群id
		data.setChatAccessHash(json.getLong("accessHash"));
		data.setChatId(json.getIntValue("chatid"));
		// }

		TLVector<TLAbsUser> users = bot.collectUsers(data.getChatId(),
				data.getChatAccessHash(), task.getIntValue("offsetNum"),
				task.getIntValue("limitNum"));
		logger.info("拉取群组用户结果：job={}，account={},size={}",
				task.getString("jobId"), task.getString("account"),
				users.size());

		int num = 0;
		// 将数据存储到数据库
		for (TLAbsUser tluser : users) {
			TLUser u = (TLUser) tluser;
			if (StringUtils.isBlank(u.getUserName()))
				continue;
			JobUser ju = new JobUser();
			ju.setJobId(task.getString("jobId"));
			ju.setAccount(task.getString("account"));
			ju.setFromGroup(data.getChatId() + "");
			ju.setUserid(u.getId() + "");
			ju.setUsername(u.getUserName());
			ju.setUserHash(u.getAccessHash());
			// u.getLangCode();
			// u.getFirstName();
			// u.getLastName();
			jobUserService.save(ju);
			num++;
		}

		if (users != null && users.size() > 0) {
			// 设置状态为已抽取
			JobTask t = jobTaskService.get(taskid);
			t.setUsernum(num);
			t.setStatus(JobTask.STATUS_FETCHED);
			jobTaskService.save(t);
		}
	}

	/**
	 * 整理任务单的用户数据。清理重复用户，计算有效用户数。
	 * 
	 * @param data
	 */
	@Transactional(readOnly = false)
	public void cleanJobUser(RequestData data) {
		jobUserService.cleanJobUser(data.getJobid());
	}

	@Transactional(readOnly = false)
	public void addUsers(RequestData data) {
		List<JobUser> jobUsers = null;
		String taskid = data.getTaskid();
		if (StringUtils.isNotBlank(taskid)) {
			//
			addUsersOfTask(data);
		} else if (StringUtils.isNotBlank(data.getJobid())) {
			// add user by jobid
			JobTask jobtask = new JobTask();
			jobtask.setJobId(data.getJobid());
			jobtask.setStatus(JobTask.STATUS_FETCHED);// 已抽取用户
			List<JobTask> list = jobTaskService.findList(jobtask);
			logger.info("任务{}共有调度拉人任务数{}", data.getJobid(), list.size());
			try {
				// 遍历执行所有记录
				for (JobTask jt : list) {
					data.setTaskid(jt.getId());
					addUsersOfTask(data);

					Thread.sleep(1000L);// 停留1s
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			throw new RuntimeException("参数无效，没有jobid or taskid");
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addUsersOfTask(RequestData data) {
		List<JobUser> jobUsers = null;
		String taskid = data.getTaskid();
		if (StringUtils.isBlank(taskid)) {
			throw new RuntimeException("参数无效，没有 taskid");
		}
		// 根据任务id，找到调用方法的参数
		JSONObject task = jobService.getRpcCallInfoByTaskid(taskid);
		data.setPhone(task.getString("account"));
		data.setJobid(task.getString("jobId"));
		data.setUrl(task.getString("groupUrl"));
		data.setChatId(task.getIntValue("chatid"));
		data.setChatAccessHash(task.getLongValue("accesshash"));// 需要拉人的群组id和访问hash

		// if (task.getInteger("chatid") == null) {
		// throw new RuntimeException("taskid="+taskid+"的用户还没加入目标群组");
		logger.warn("taskid={}的用户{}还没加入目标群组", taskid, data.getPhone());
		logger.info("{}加入群组{}", data.getPhone(), data.getUrl());
		IBot bot = getBot(data);

		JSONObject json = bot.importInvite(data.getUrl());
		data.setChatAccessHash(json.getLong("accessHash"));
		data.setChatId(json.getIntValue("chatid"));
		// }

		// 取用户列表
		JobUser jobUser = new JobUser();
		jobUser.setAccount(data.getPhone());
		jobUser.setJobId(task.getString("jobId"));
		jobUsers = jobUserService.findList(jobUser);

		if (jobUsers != null && jobUsers.size() > 0) {
			// IBot bot = getBot(data);
			// 加入目标群组
			// bot.importInvite(data.getUrl());

			bot.addUsers(data.getChatId(), data.getChatAccessHash(), jobUsers);

			// 标记任务已完成
			if (StringUtils.isNotBlank(taskid)) {
				// 设置状态为已完成
				JobTask t = jobTaskService.get(taskid);
				t.setStatus(JobTask.STATUS_JOIN);// 已加人
				jobTaskService.save(t);
			}
		} else {
			throw new RuntimeException("not find jobuser by account="
					+ data.getPhone() + " in job " + data.getJobid());
		}
	}

	@Transactional(readOnly = false)
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

	public IBot getBotByPhone(String phone) {
		IBot bot = bots.get(phone);
		if (bot == null) {
			throw new RuntimeException(phone + "账号实例不存在");
		}
		return bot;
	}

	/**
	 * 查看group详细信息
	 * 
	 * @param data
	 * @return
	 */
	@Transactional(readOnly = false)
	public JSONObject groupInfo(RequestData data) {
		// 通过管理员账号获取信息
		IBot bot = bots.get(getAdminAccount());
		if (bot == null) {
			throw new RuntimeException(getAdminAccount() + "账号实例不存在1");
		}
		JSONObject json = new JSONObject();
		Chat chat = new Chat();
		chat.setAccount(getAdminAccount());
		chat.setChatid(data.getChatId() + "");
		List<Chat> list = chatService.findList(chat);
		if (list.size() > 0) {
			Chat c = list.get(0);
			json = bot.getGroupInfo(Integer.parseInt(c.getChatid()),
					c.getAccesshash(), c.getIsChannel() == 1 ? true : false);
		}
		return json;
	}

	@Transactional(readOnly = false)
	public JSONObject updateGroupInfoByLink(String groupUrl) {
		RequestData data = new RequestData();
		JSONObject result = new JSONObject();
		try {
			data.setPhone(getAdminAccount());
			IBot bot = getBotByPhone(getAdminAccount());
			// 通过加入群组，获取群组id
			JSONObject json = bot.importInvite(groupUrl);
			if (json.getBooleanValue("success")) {
				Integer groupid = json.getInteger("chatid");
				data.setChatId(groupid);
				// 通过群组id获取群组详细信息
				JSONObject ginfo = groupInfo(data);

				// 保存群组信息
				Group group = new Group();
				group.setId(groupid + "");
				group.setUrl(groupUrl);
				group.setName(ginfo.getString("title"));
				group.setUsername(ginfo.getString("username"));
				group.setUsernum(ginfo.getInteger("usernum"));
				groupService.insertOrUpdate(group);

				result.put("groupid", groupid);
				result.put("name", group.getName());
			} else {
				throw new RuntimeException("通过url  加入群组失败！");
			}
		} catch (Exception e) {
			logger.error("通过url更新群组信息操作失败", e);
			throw new RuntimeException("通过url更新群组信息操作失败", e);
		}

		return result;
	}

	/**
	 * 在某账号下查找用户名和群组。
	 * 
	 * @param phone
	 * @param username
	 * @return
	 */
	public JSONObject searchUser(String phone, String username) {
		IBot bot = getBotByPhone(phone);
		return bot.searchUser(username);
	}
}
