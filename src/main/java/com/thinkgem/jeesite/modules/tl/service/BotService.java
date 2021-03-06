package com.thinkgem.jeesite.modules.tl.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBot;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.XUtils;
import org.telegram.plugins.xuser.ex.UnvalidateAccountException;
import org.telegram.plugins.xuser.work.BotManager;
import org.telegram.plugins.xuser.work.BotPool;
import org.telegram.plugins.xuser.work.BotWrapper;
import org.telegram.plugins.xuser.work.TaskExecutor;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.entity.Chat;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.entity.TlUser;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * @author ThinkPad
 *
 */
@Service
@Transactional(readOnly = true)
public class BotService implements BotManager {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Logger slog = LoggerFactory.getLogger("com.telegram.success");
	protected Logger phonelog = LoggerFactory
			.getLogger("com.telegram.reg.phone");
	// 客户
	// private static final int APIKEY = 314699; // your api key
	// private static final String APIHASH = "870567202befc11fee16aa1fdea1bc37";
	// // your
	// private String adminAccount = "8617075494722";
	// private String adminAccount = "8613544252494";
	// private static final int APIKEY = 432207; // your api key
	// private static final String APIHASH = "3ebb326385fa410f83e4b33efd7ea1f4";
	// // your
	// private String adminAccount ="8617075491348";// "8613726447007";

	// private static final int APIKEY = 202491; // your api key
	// private static final String APIHASH = "9f32d44fca581599dbbe02cec25ffe58";
	// //
	// your
	// private String adminAccount="8618566104318";

	private static final int APIKEY = Integer.parseInt(Global
			.getConfig("tl.apikey")); // your api key
	private static final String APIHASH = Global.getConfig("tl.apihash"); // your
	private String adminAccount = Global.getConfig("tl.admin.account");// 管理员账号

	private Map<String, IBot> bots = new HashMap<String, IBot>();
	private Map<String, IBot> regbots = new HashMap<String, IBot>();
	@Autowired
	private BotFactory botFactory;

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
	@Autowired
	private TlUserService tlUserService;
	@Autowired
	private RegistePoolService registePoolService;
	@Autowired
	private DefaultWorkService defaultWorkService;

	@Resource(name = "transactionManager")
	private DataSourceTransactionManager transactionManager;

	private BotPool botPool = null;

	// 全局jobid，待完善
	private String jobid;

	public BotService() {
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
		if ("true".equals(Global.getConfig("tl.autoRun"))) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
						// 初始化账号
						accountInit(null);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}

		// 创建bot池
		botPool = new BotPool(this);
		// !!!!!!!!!!!!!!!!!!!!!!!!!!
		// 增加任务执行
		TaskExecutor executor = new TaskExecutor(this, jobService,
				defaultWorkService);
		botPool.addObserver(executor);
		// !!!!!!!!!!!!!!!!!!!!!!!!
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
			if (ins != null)
				ins.stop();
			// 去除引用
			// bots.remove(botkey);
			iterator.remove();
		}
		// 3.管理员账号登录
		/*
		 * logger.info("启动管理员账号"); XUserBot bot = botFactory.createBot();
		 * bot.setBotDataService(botDataService); LoginStatus status =
		 * bot.start(getAdminAccount(), APIKEY, APIHASH); if (status ==
		 * LoginStatus.ALREADYLOGGED) { bots.put(getAdminAccount(), bot);
		 * bot.setStatus(XUserBot.STATUS_OK);
		 * 
		 * // 更改帐号状态为run updateAccountState(getAdminAccount(),
		 * Account.STATUS_RUN); } else { String title = getAdminAccount() +
		 * "管理员登录失败，status" + status; logger.error(title); LogUtils.saveLog(new
		 * Log("tl", title), null); }
		 */
		return null;
	}

	/**
	 * 管理员账号
	 * 
	 * @return
	 */
	public String getAdminAccount() {
		// TODO Auto-generated method stub
		return adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
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
		LoginStatus status = null;
		try {
			XUserBot bot = (XUserBot) bots.get(data.getPhone());
			if (bot == null) {
				bot = botFactory.createBot();
				bot.setBotDataService(botDataService);
				bots.put(data.getPhone(), bot);
			} else {
				// 已经登陆过l
				if (XUserBot.STATUS_OK.equals(bot.getStatus())) {
					return LoginStatus.ALREADYLOGGED;
				}
			}
			status = bot.start(data.getPhone(), APIKEY, APIHASH);
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
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.error("start", e.getMessage());
			if (StringUtils.isNotBlank(msg) && msg.contains("ERRORSENDINGCODE")) {
				// 登录失败，删除账号和auth文件
				removeAccount(data.getPhone());
			}
			bots.put(data.getPhone(), null);
			status = null;
		}
		return status;
	}

	/**
	 * 删除账号。
	 * 
	 * @param phone
	 */
	@Transactional(readOnly = false)
	public void removeAccount(String phone) {
		try {
			accountService.delete(new Account(phone));
			// 删除文件
			deleteAuthFile(phone);
		} catch (Exception e) {
			logger.error("删除账号{},error={}", phone, e.getMessage());
		}
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
	@Deprecated
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

	// @Transactional(readOnly = false)
	@Transactional(readOnly = false)
	public void collectUsersOfTask(RequestData data, String taskid) {
		// 根据任务id，找到调用方法的参数
		JSONObject task = jobTaskService.getRpcCallInfo(taskid);
		if (task == null) {
			throw new RuntimeException(taskid + "任务不存在！");
		}
		data.setPhone(task.getString("account"));
		data.setUrl(task.getString("groupUrl"));
		data.setChatAccessHash(task.getLongValue("accessHash"));
		data.setChatId(task.getIntValue("chatid"));
		try {

			IBot bot = getBot(data, true);

			// if (task.getInteger("chatid") == null) {
			// throw new RuntimeException("taskid="+taskid+"的用户还没加入来源群组");
			if (data.getChatAccessHash() == 0) {
				logger.warn("taskid={}的用户{}还没加入来源群组", taskid, data.getPhone());
				logger.info("{}加入群组{}", data.getPhone(), data.getUrl());
				JSONObject json = bot.importInvite(data.getUrl());

				// 来源群id
				if (json.getLong("accessHash") == null) {
					logger.info("{}加入群组{} failure ", data.getPhone(),
							data.getUrl());
				} else {
					data.setChatAccessHash(json.getLong("accessHash"));
					data.setChatId(json.getIntValue("chatid"));
				}
			}

			Group g = groupService.get(data.getChatId() + "");
			if (g == null) {
				//
				throw new RuntimeException("群组id=" + data.getChatId()
						+ "在表tl_group 中不存在！");
			}
			int offset = g.getOffset() == null ? 0 : g.getOffset();
			int limitNum = data.getLimit() == 0 ? 50 : data.getLimit();

			TLVector<TLAbsUser> users = bot.collectUsers(data.getChatId(),
					data.getChatAccessHash(), offset, limitNum);

			if (users == null)
				users = new TLVector<TLAbsUser>();
			logger.info("拉取群组用户结果：job={}，account={},size={}",
					task.getString("jobId"), task.getString("account"),
					users != null ? users.size() : 0);

			// 更新群组的offset
			g.setOffset(offset + limitNum);
			groupService.updateOffset(g);

			int num = 0;
			// 将数据存储到数据库
			for (TLAbsUser tluser : users) {
				TLUser u = (TLUser) tluser;
				if (StringUtils.isBlank(u.getUserName())) {
					logger.debug("用户没有username，忽略");
					continue;
				}
				String firstName = XUtils.transChartset(u.getFirstName());
				String lastName = XUtils.transChartset(u.getLastName());
				if (firstName != null
						&& ((firstName.length() > 100
								|| lastName.length() > 100 || (firstName
								.contains("拉人") || lastName.contains("电报群"))))) {
					logger.debug("用户名长度大于100，存在  拉人  电报群 字样，忽略");
					continue;
				}

				JobUser ju = new JobUser();
				ju.setJobId(task.getString("jobId"));
				ju.setTaskId(taskid);
				ju.setAccount(task.getString("account"));
				ju.setFromGroup(data.getChatId() + "");
				ju.setUserid(u.getId() + "");
				ju.setUsername(u.getUserName());
				ju.setUserHash(u.getAccessHash());
				ju.setFirstname(firstName);
				ju.setLastname(lastName);
				ju.setStatus("0");
				// u.getLangCode();
				// u.getFirstName();
				// u.getLastName();
				// jobUserService.save(ju);
				jobUserService.insertUserToJob(ju, task.getString("jobId"));

				TlUser tlu = new TlUser();
				tlu.setId(ju.getUserid());
				tlu.setFirstname(ju.getFirstname());
				tlu.setLastname(ju.getLastname());
				tlu.setUsername(ju.getUsername());
				tlu.setMsgTime(null);
				tlu.setLangcode(u.getLangCode());
				tlu.setUpdateDate(new Date());
				tlu.setMsgNum(0);
				tlu.setUserstate(u.getStatus() == null ? null : u.getStatus()
						.toString());
				// 同时写入tl_user表
				tlUserService.insertOrUpdate(tlu);
				num++;
			}

			if (users != null && users.size() > 0) {
				// 设置状态为已抽取
				JobTask t = jobTaskService.get(taskid);
				t.setUsernum(num);
				t.setStatus(JobTask.STATUS_FETCHED);
				jobTaskService.save(t);
			}

			// 超过10000个账号限制
			if (offset + limitNum > 10000) {
				logger.warn("job={},account={},拉取群组{}达到上限10000，停止抽取。",
						task.getString("jobId"), data.getPhone());
			}
		} catch (Exception e) {
			logger.error("采集任务失败,phone={},error={}", data.getPhone(),
					e.getMessage());

		} finally {
			//
			bots.put(data.getPhone(), null);
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
		// logger.warn("taskid={}的用户{}还没加入目标群组", taskid, data.getPhone());
		logger.info("{}加入群组{}", data.getPhone(), data.getUrl());
		IBot bot = getBot(data, true);

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

			// 将tl_job_user表中记录标记为已邀请
			jobUser.setStatus("1");
			jobUserService.updateStatus(jobUser);
		} else {
			throw new RuntimeException("在账号下没找到用户， account=" + data.getPhone()
					+ " ，job= " + data.getJobid());
		}
	}

	@Transactional(readOnly = false)
	public boolean stop(RequestData data) {
		IBot bot = getBot(data);
		boolean success = bot.stop();

		bots.remove(data.getPhone());
		return success;
	}

	private IBot getBot(RequestData data) {
		return getBot(data, false);
	}

	private IBot getBot(RequestData data, boolean start) {
		IBot bot = bots.get(data.getPhone());
		if (bot == null) {
			if (start) {
				// 启动账号
				start(data);
				bot = bots.get(data.getPhone());
				if (bot == null) {
					throw new RuntimeException(data.getPhone() + "账号启动失败");
				}
			} else {
				throw new RuntimeException(data.getPhone() + "账号实例不存在");
			}
		}

		if (bot.isAuthCancel()) {
			bots.put(data.getPhone(), null);
			removeAccount(data.getPhone());
			throw new RuntimeException(data.getPhone() + "账号失效");
		}
		return bot;
	}

	public IBot getBotByPhone(String phone) {
		return getBotByPhone(phone, false);
	}

	public IBot getBotByPhone(String phone, boolean start) {
		IBot bot = bots.get(phone);
		if (bot == null) {
			if (start) {
				// 启动账号
				RequestData data = new RequestData();
				data.setPhone(phone);
				start(data);
				bot = bots.get(phone);
				if (bot == null) {
					throw new RuntimeException(phone + "账号启动失败");
				} else {
					// 等待1s，便于检查isAuthCancel，auth cancel是异步回调方法回写
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			} else {
				// throw new RuntimeException(phone + "账号实例不存在");
				bot = null;
			}
		}

		if (bot != null && bot.isAuthCancel()) {
			bots.put(phone, null);
			removeAccount(phone);
			throw new UnvalidateAccountException(phone + "账号失效");
		}
		return bot;
	}

	public IBot getRegBotByPhone(String phone, boolean start) {
		IBot bot = regbots.get(phone);
		if (bot == null) {
			if (start) {
				// 启动账号
				RequestData data = new RequestData();
				data.setPhone(phone);
				start(data);
				bot = regbots.get(phone);
				if (bot == null) {
					throw new RuntimeException(phone + "账号启动失败");
				} else {
					// 等待1s，便于检查isAuthCancel，auth cancel是异步回调方法回写
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			} else {
				// throw new RuntimeException(phone + "账号实例不存在");
				bot = null;
			}
		}

		if (bot != null && bot.isAuthCancel()) {
			regbots.put(phone, null);
			removeAccount(phone);
			throw new UnvalidateAccountException(phone + "账号失效");
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
			IBot bot = getBotByPhone(getAdminAccount(), true);
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
				group.setIsChannel("1");
				group.setUsernum(ginfo.getInteger("usernum"));
				group.setUpdateNum(0);
				group.setOut("0");
				group.setStatus("0");
				groupService.insertOrUpdate(group);

				result.put("usernum", group.getUsernum());
				result.put("groupid", groupid);
				result.put("name", group.getName());
			} else {
				throw new RuntimeException("通过url  加入群组失败！");
			}
		} catch (Exception e) {
			logger.error("通过url更新群组信息操作失败");
			throw new RuntimeException("通过url更新群组信息操作失败", e);
		}

		return result;
	}

	/**
	 * 通过群组link获取id;
	 * 
	 * @param url
	 * @return
	 */
	@Transactional(readOnly = false)
	public int getGroupidByUrl(String url) {
		int groupid = 0;
		Group g = new Group();
		g.setUrl(url);
		List<Group> list = groupService.findList(g);
		if (list.size() > 0) {
			g = list.get(0);
			groupid = Integer.parseInt(g.getId());
		} else {
			// 用管理员加入群组
			IBot bot = getBotByPhone(getAdminAccount(), true);
			JSONObject json = bot.importInvite(url);
			groupid = json.getIntValue("chatid");

			if (groupid == 0) {
				throw new RuntimeException("加入群组失败，url=" + url);

			}
		}
		return groupid;
	}

	/**
	 * 在某账号下查找用户名和群组。
	 * 
	 * @param phone
	 * @param username
	 * @return
	 */
	@Transactional(readOnly = false)
	public JSONObject searchUser(String phone, String username) {
		IBot bot = getBotByPhone(phone);
		return bot.searchUser(username);
	}

	/**
	 * 注册单个账号。
	 * 
	 * @param phone
	 */
	@Transactional(readOnly = false)
	public boolean registe(String phone) {
		boolean success = false;
		logger.debug("注册，发送验证码，{}", phone);
		String status = "FAILUE";
		// 1.先检查账号是否已经注册过了
		Account ac = accountService.findAccountInHis(phone);
		boolean save = true;
		if (ac != null) {
			save = false;
			if ("SUCCESS".equals(ac.getStatus())) {
				logger.warn("账号{}已经在数据库中，不用注册了", phone);
				return success;
			}
			if ("FAILURE".equals(ac.getStatus())) {
				// 2.检查账号是否已经处理过。有可能账号已经试过不成功，黑名单
				logger.warn("账号{}提交注册失败，已列入黑名单", phone);
				return success;
			} else {
				// 记录尝试次数+1
				ac.setTrynum(ac.getTrynum() + 1);
				ac.setUpdateDate(new Date());
				accountService.updateAccountHis(ac);
				logger.warn("账号{}注册失败，try again", phone);

				regbots.put(phone, null);
			}

		}

		XUserBot bot = (XUserBot) regbots.get(phone);
		if (bot == null) {
			bot = botFactory.createBot();
			bot.setBotDataService(botDataService);
			regbots.put(phone, bot);
		} else {
			// 已经登陆过
			throw new RuntimeException("账号" + phone + "实例已经创建，说明已经操作过了");
		}

		// 注册
		JSONObject json = bot.registe(phone, APIKEY, APIHASH);
		if ("success".equals(json.getString("result"))) {
			// 成功
			status = "SENTCODE";
			success = true;
			bot.setStatus(XUserBot.STATUS_WAIT);
			// 等待输入验证码
		} else if ("timeout".equals(json.getString("result"))) {
			// timeout
			status = "TIMEOUT";
			save = false;// dont save
			// TRY AGAIN
			logger.info("超时，重新发送验证码……");
			json = bot.registe(phone, -1, APIHASH);
			if ("success".equals(json.getString("result"))) {
				// 成功
				status = "SENTCODE";
				bot.setStatus(XUserBot.STATUS_WAIT);
				success = true;
				save = true;
				// 等待输入验证码
			} else if ("timeout".equals(json.getString("result"))) {
				// timeout
				status = "TIMEOUT";
				save = false;// dont save
				deleteAuthFile(phone);
			} else {
				bot.stop();
				regbots.put(phone, null);
				deleteAuthFile(phone);
			}
		} else {

			bot.stop();
			// 失败,移除map
			regbots.put(phone, null);
			// FIXME 删除认证文件
			status = "FAILURE";
			logger.error("{}其他异常，注册失败,{}", phone, json.getString("status"));

			deleteAuthFile(phone);

		}
		if (save) {
			// 保存try记录
			ac = new Account();
			ac.setIsNewRecord(true);
			ac.setId(phone);
			ac.setTrynum(1);
			ac.setStatus(status);
			ac.setUpdateDate(new Date());
			ac.setRole("0");
			ac.setCreateBy(new User("1"));
			ac.setUpdateBy(new User("1"));
			accountService.insertAccountHis(ac);
		}

		return success;
	}

	private void deleteAuthFile(String phone) {
		try {
			// File auth = new File("auth/" + phone + ".auth");
			// auth.deleteOnExit();
			String path = Global.getConfig("tl.auth.path") + phone + ".auth";
			String bakpath = Global.getConfig("tl.auth.path") + "authdel"
					+ File.separator + phone + ".auth";
			File bakauth = new File(Global.getConfig("tl.auth.path")
					+ "authdel");
			if (!bakauth.exists())
				bakauth.mkdir();

			// 备份文件，避免误删除
			FileUtils.copyFile(path, bakpath);

			File authfile = new File(path);
			authfile.deleteOnExit();
		} catch (Exception e) {
			logger.warn("delete auth file error {}", e.getMessage());
		}
	}

	@Transactional(readOnly = false)
	public JSONObject setRegAuthCode(String phone, String code) {
		return setRegAuthCode(phone, code, false);
	}

	@Transactional(readOnly = false)
	public JSONObject setRegAuthCode(String phone, String code,
			boolean startWork) {
		IBot bot = getRegBotByPhone(phone, false);
		if (bot == null) {
			JSONObject json = new JSONObject();
			json.put("result", false);
			return json;
		}
		XUserBot b = (XUserBot) bot;
		if (!XUserBot.STATUS_WAIT.equals(b.getStatus())) {
			throw new RuntimeException(phone + "账号实例不是在等待输入验证码状态");
		}
		String status = null;
		JSONObject json = bot.setRegAuthCode(phone, code);
		if (json.getBooleanValue("result")) {
			logger.info("{}，注册成功", phone, code);
			status = "SUCCESS";

		} else {
			if ("timeout".equals(json.getString("type"))) {
				// try again
				json = bot.setRegAuthCode(phone, code);
				if (json.getBooleanValue("result")) {
					logger.info("{}，注册成功", phone, code);
					status = "SUCCESS";
				} else {
					if ("timeout".equals(json.getString("type"))) {
						status = "TIMEOUT";
					} else {
						status = "FAILURE";
						// 清除
						regbots.put(phone, null);
						bot.stop();
					}
				}
			} else {
				// 失败,列入黑名单
				status = "FAILURE";
				// 清除
				bot.stop();
				regbots.put(phone, null);

				// 删除auth文件
				removeAccount(phone);
			}
		}

		// 更新数据库
		Account ac = accountService.findAccountInHis(phone);
		if (ac != null) {
			ac.setStatus(status);
			ac.setRemarks(json.getString("msg"));
			ac.setUpdateDate(new Date());
			accountService.updateAccountHis(ac);
		} else {
			logger.error("{},不在数据库中,!!??");
		}
		// 成功，则写入账号表
		if ("SUCCESS".equals(status)) {
			ac = new Account();
			ac.setIsNewRecord(true);
			ac.preInsert();
			ac.setId(phone);
			ac.setName(json.getString("firstName") + " "
					+ json.getString("lastName"));
			ac.setStatus("ready");
			ac.setRole("0");
			ac.setUsernum(0);
			ac.setGroupnum(0);
			accountService.save(ac);

			// 写入日志文件
			phonelog.info("{},成功", phone);

			// 设置用户密码，防止被占用
			// TODO 设置用户密码，防止被占用
			// setAccountPassword(phone);

			if (startWork) {
				// 加入任务队列
				addBot(getJobid(), phone);
			}
		}
		return json;
	}

	private String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	/**
	 * 设置用户名密码。
	 * 
	 * @param phone
	 */
	@Transactional(readOnly = false)
	public void setAccountPassword(String phone) {
		IBot bot = getBotByPhone(phone, true);
		String password = Global.getConfig("tl.account.password");// "xln2018";//
																	// 默认的统一密码
		if (password == null)
			password = "xln2018";

		String hint = Global.getConfig("tl.account.password.hint");// "已被你大爷占用了，不送！ 三人行留字";
		if (hint == null)
			hint = "ye zan yong le ,bye! san ren xing 2018";

		boolean success = bot.setAccountPassword(phone, password, hint);
		if (success) {
			// 标记账号为已设置密码
			accountService.updatePwdLock(phone);
		}
	}

	/**
	 * 将未设置密码的账号添加密码。
	 * 
	 * @param data
	 */
	@Transactional(readOnly = false)
	public void setAccountPwd(RequestData data) {
		Account account = new Account();
		account.setPwdLock(Global.NO);
		account.setId(data.getPhone());
		List<Account> list = accountService.findList(account);
		for (Account ac : list) {
			setAccountPassword(ac.getId());
		}
	}

	/**
	 * 
	 */
	@Transactional(readOnly = false)
	public void addBot(String jobid, String phone) {
		XUserBot bot = null;
		try {
			bot = (XUserBot) getBotByPhone(phone, true);
			bot.setPhone(phone);
			bot.setJobid(jobid);
			BotWrapper botw = new BotWrapper(jobid, bot);
			botPool.addBot(botw);
		} catch (UnvalidateAccountException e) {
			// 删除账号
			if (bot != null)
				deleteBot(bot, "失效账号");
		} catch (Exception e) {
			logger.error("账号启动失败！");

		}
	}

	@Override
	public void destroy(XUserBot bot) {

		bot.stop();
		bots.put(bot.getPhone(), null);
		// System.out.println("清除账号");
	}

	@Override
	public void deleteBot(XUserBot bot, String error) {
		logger.info("{}，{}任务失败，删除账号,error={}", bot.getJobid(), bot.getPhone(),
				error);
		System.out.println("删除账号");
	}

	/**
	 * 停止运行。先停止注册手机号，待调度执行完毕。
	 * 
	 * @param jobid
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean stopJob(String jobid) {
		this.jobid = jobid;
		boolean success = true;
		try {
			registePoolService.stop();
			jobService.stop();
			// 拉人线程池停止
			botPool.stop();
		} catch (Exception e) {
			success = true;
		}
		return success;
	}

	/**
	 * 停止注册。
	 * 
	 * @param jobid
	 * @return
	 */
	public boolean stopReg(String jobid) {
		this.jobid = jobid;
		boolean success = true;
		try {
			registePoolService.stop();
		} catch (Exception e) {
			success = true;
		}
		return success;
	}

	/**
	 * 开始注册。
	 * 
	 * @param num
	 *            注册数量
	 * 
	 * @param jobid
	 * @return
	 */
	public boolean startReg(int num) {
		boolean success = true;
		try {
			registePoolService.startWork(num, false);
		} catch (Exception e) {
			success = true;
		}
		return success;
	}

	/**
	 * 启动。 启动账号注册，注册成功后，放入拉人的线程池。 启动，需要将job的详细信息，包括采集群的信息加载到缓存。
	 * 
	 * @param jobid
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean startJob(String jobid) {
		this.jobid = jobid;
		boolean success = true;
		if (botPool.isRun()) {
			logger.error("程序正在运行，不能重复执行");
//			throw new RuntimeException("程序正在运行，不能重复执行");
			// return false;
		}
		// try {
		Job job = jobService.get(jobid);
		if (job != null) {
			slog.info("{}任务开始，需要拉人数{}，约需要账号{},{}", job.getId(),
					job.getUsernum(), job.getAccountNum(), job.getGroupUrl());
			// 设置总人数
			BotWrapper.setPlanTotal(job.getUsernum());
			// BotWrapper.setSuccessTotal(new AtomicInteger(0));
			botPool.start();// 设置标识位
			// 初始化job数据，共获取数据
			int jobGroupNum = jobService.initRunData(job);
			if (jobGroupNum == 0) {
				throw new RuntimeException("没有可用的采集群组，可能没设置，可能已到达上线");
			}

			// 新线程处理，当数据库中存在很多账号时，不能全部一次放进去
			// Thread thread = new Thread() {
			// public void run() {
			// // 将数据库中的账号放入池子
			// addDbAccountToBotPool(jobid);
			// };
			// };
			// thread.setDaemon(true);
			// thread.start();

			// 以账号数量为依据，决定job是否停止
			registePoolService.startWork(job.getAccountNum(), true);

		} else {
			success = false;
			slog.warn("{}任务不存在！", jobid);
		}
		// } catch (Exception e) {
		// success = false;
		// }
		return success;
	}

	/**
	* 
	*/
	public void addDbAccountToBotPool(String jobid) {
		Account account = new Account();
		// TODO 查询可用账号，放入队列
		// 可用，1）针对本job，加入用户数未达到40人
		// account.setRole("0");
		List<Account> list = accountService.findAvalidList(account);
		slog.warn(" 查询可用账号，放入队列,size={}", list.size());
		int i = 0;
		for (Account ac : list) {
			try {
				Thread.sleep((i++) * 5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			addBot(jobid, ac.getId());
			System.out.println(ac.getId() + "账号放入池子");
		}
	}

	public RegistePoolService getRegistePoolService() {
		return registePoolService;
	}

	public void setRegistePoolService(RegistePoolService registePoolService) {
		this.registePoolService = registePoolService;
	}

	public BotFactory getBotFactory() {
		return botFactory;
	}

	public void setBotFactory(BotFactory botFactory) {
		this.botFactory = botFactory;
	}

	/*
	 * 停止注册。
	 * 
	 * @see org.telegram.plugins.xuser.work.BotManager#stopReg()
	 */
	@Override
	public void stopReg() {
		try {
			registePoolService.stop();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAccountRunResult(String phone, int usernum, int total,
			String acstatus, String remark) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		// // 事物隔离级别，开启新事务，这样会比较安全些。
		TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态

		try {
			// 标记账号已成功完成任务
			Account ac = new Account();
			ac.setId(phone);
			ac.setStatus(acstatus);
			// ac.setRole("0");
			ac.setUsernum(usernum);
			ac.setRemarks(remark);
			ac.setUpdateDate(new Date());
			accountService.updateSuccess(ac);

			// 事务提交
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
		}

		// 判断总任务是否完成
		if (total > BotWrapper.getPlanTotal()) {
			slog.error("已完成拉人任务，总拉人人数{}~~~~~~~~~~~~~~~~~~~~~~~~~~", total);
			stopJob(jobid);
			System.out
					.println("=========================================================================================================");
			System.out
					.println("=========================================================================================================");
			System.out
					.println("=========================================================================================================");
		}
	}

}
