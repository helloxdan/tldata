package com.thinkgem.jeesite.modules.tl.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBot;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.ex.UnvalidateAccountException;
import org.telegram.plugins.xuser.work.BotManager;
import org.telegram.plugins.xuser.work.BotPool;
import org.telegram.plugins.xuser.work.BotWrapper;
import org.telegram.plugins.xuser.work.TaskExecutor;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.entity.Job;
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
	private Map<String, IBot> regbots = bots;// new HashMap<String, IBot>();
	@Autowired
	private BotFactory botFactory;
	@Autowired
	private BotDataService botDataService;

	@Autowired
	private JobService jobService;

	@Autowired
	private RegistePoolService registePoolService;
	@Autowired
	private RegisteService registeService;
	@Autowired
	private DefaultWorkService defaultWorkService;

	private BotPool botPool = null;

	// 全局jobid，待完善
	private String jobid;

	public BotService() {
	}

	/**
	 * 定时调度，隔时间段执行一次
	 */
	@Transactional(readOnly = false)
	public void scheduleRunWork() {
		// 从队列中取一个bot，并执行
		int size = botPool.poll();
		logger.debug("定时调度，采集拉人队列，size={}", size);
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
		// accountService.resetAccountStatus();
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

	public BotPool getBotPool() {
		return botPool;
	}

	public void setBotPool(BotPool botPool) {
		this.botPool = botPool;
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
				// updateAccountState(data.getPhone(), Account.STATUS_RUN);
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
			// accountService.delete(new Account(phone));
			// 删除文件
			deleteAuthFile(phone);
		} catch (Exception e) {
			logger.error("删除账号{},error={}", phone, e.getMessage());
		}
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
		// Account ac = accountService.findAccountInHis(phone);
		boolean save = true;
		// if (ac != null) {
		// save = false;
		// if ("SUCCESS".equals(ac.getStatus())) {
		// logger.warn("账号{}已经在数据库中，不用注册了", phone);
		// return success;
		// }
		// if ("FAILURE".equals(ac.getStatus())) {
		// // 2.检查账号是否已经处理过。有可能账号已经试过不成功，黑名单
		// logger.warn("账号{}提交注册失败，已列入黑名单", phone);
		// return success;
		// } else {
		// // 记录尝试次数+1
		// ac.setTrynum(ac.getTrynum() + 1);
		// ac.setUpdateDate(new Date());
		// accountService.updateAccountHis(ac);
		// logger.warn("账号{}注册失败，try again", phone);
		//
		// regbots.put(phone, null);
		// }
		//
		// }

		XUserBot bot = (XUserBot) regbots.get(phone);
		if (bot == null) {
			bot = botFactory.createBot();
			bot.setBotDataService(botDataService);
			regbots.put(phone, bot);
		} else {
			// 已经登陆过
			throw new RuntimeException("账号" + phone + "实例已经创建，说明已经操作过了");
		}
		// //启动和发送认证码隔开时间差
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
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
			// ac = new Account();
			// ac.setIsNewRecord(true);
			// ac.setId(phone);
			// ac.setTrynum(1);
			// ac.setStatus(status);
			// ac.setUpdateDate(new Date());
			// ac.setRole("0");
			// ac.setCreateBy(new User("1"));
			// ac.setUpdateBy(new User("1"));
			// accountService.insertAccountHis(ac);
		}

		return success;
	}

	private void deleteAuthFile(String phone) {
		try {
			// File auth = new File("auth/" + phone + ".auth");
			// auth.deleteOnExit();
			String path = Global.getConfig("tl.auth.path") + phone + ".auth";
			// String bakpath = Global.getConfig("tl.auth.path") + "authdel"
			// + File.separator + phone + ".auth";
			// File bakauth = new File(Global.getConfig("tl.auth.path")
			// + "authdel");
			// if (!bakauth.exists())
			// bakauth.mkdir();

			// 备份文件，避免误删除
			// FileUtils.copyFile(path, bakpath);

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
					logger.debug("{}，注册成功", phone, code);
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

		// 成功，则写入账号表
		if ("SUCCESS".equals(status)) {
			// 写入日志文件
			phonelog.info("{}", phone);
			if (startWork) {
				// 将注册的bot关闭，运行任务时，重新启动
				// bot.stop();
				// 加入任务队列
				addBot(getJobid(), phone);
			}
		}
		/*
		 * // 更新数据库 Account ac = accountService.findAccountInHis(phone); if (ac
		 * != null) { ac.setStatus(status);
		 * ac.setRemarks(json.getString("msg")); ac.setUpdateDate(new Date());
		 * accountService.updateAccountHis(ac); } else {
		 * logger.error("{},不在数据库中,!!??"); } // 成功，则写入账号表 if
		 * ("SUCCESS".equals(status)) { ac = new Account();
		 * ac.setIsNewRecord(true); ac.preInsert(); ac.setId(phone);
		 * ac.setName(json.getString("firstName") + " " +
		 * json.getString("lastName")); ac.setStatus("ready"); ac.setRole("0");
		 * ac.setUsernum(0); ac.setGroupnum(0); accountService.save(ac);
		 * 
		 * // 写入日志文件 phonelog.info("{},成功", phone);
		 * 
		 * // 设置用户密码，防止被占用 // TODO 设置用户密码，防止被占用 // setAccountPassword(phone);
		 * 
		 * if (startWork) { // 加入任务队列 addBot(getJobid(), phone); } }
		 */
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
			// accountService.updatePwdLock(phone);
		}
	}

	/**
	 * 将未设置密码的账号添加密码。
	 * 
	 * @param data
	 */
	@Transactional(readOnly = false)
	public void setAccountPwd(RequestData data) {
		// Account account = new Account();
		// account.setPwdLock(Global.NO);
		// account.setId(data.getPhone());
		// List<Account> list = accountService.findList(account);
		// for (Account ac : list) {
		// setAccountPassword(ac.getId());
		// }
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
		stopJob(bot.getJobid());
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
			// registePoolService.stop();

			registeService.stop();// 停止注册
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
			// registePoolService.stop();
			registeService.stop();

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

	@Transactional(readOnly = false)
	public boolean startJob(Job job) {
		this.jobid = job.getId();
		boolean success = true;
		if (botPool.isRun()) {
			logger.error("程序正在运行，不能重复执行");
			// throw new RuntimeException("程序正在运行，不能重复执行");
			// return false;
		}

		slog.info("{}任务开始，需要拉人数{}，约需要账号{},{}", job.getId(), job.getUsernum(),
				job.getAccountNum(), job.getGroupUrl());
		// 设置总人数
		BotWrapper.setPlanTotal(job.getUsernum());
		// BotWrapper.setSuccessTotal(new AtomicInteger(0));
		botPool.start();// 设置标识位

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
		List<Account> list = null;// accountService.findAvalidList(account);
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
			// registePoolService.stop();
			registeService.stop();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateAccountRunResult(String phone, int usernum, int total,
			String acstatus, String remark) {

		// 关闭bot
		IBot bot = getBotByPhone(phone);
		if (bot != null)
			bot.stop();
		bots.put(phone, null);

		// DefaultTransactionDefinition def = new
		// DefaultTransactionDefinition();
		// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		// //
		// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		// // // 事物隔离级别，开启新事务，这样会比较安全些。
		// TransactionStatus status = transactionManager.getTransaction(def); //
		// 获得事务状态
		//
		// try {
		// // 标记账号已成功完成任务
		// Account ac = new Account();
		// ac.setId(phone);
		// ac.setStatus(acstatus);
		// // ac.setRole("0");
		// ac.setUsernum(usernum);
		// ac.setRemarks(remark);
		// ac.setUpdateDate(new Date());
		// accountService.updateSuccess(ac);
		//
		// // 事务提交
		// transactionManager.commit(status);
		// } catch (Exception e) {
		// transactionManager.rollback(status);
		// }

		// 判断总任务是否完成
		if (total > BotWrapper.getPlanTotal()) {
			slog.error("已完成拉人任务，总拉人人数{}~~~~~~~~~~~~~~~~~~~~~~~~~~", total);
			stopJob(jobid);
			System.out
					.println("=========================================================================================================");
			System.out
					.println("==========================================退出=============================================================");
			System.out
					.println("=========================================================================================================");
			// 停止10秒，然后退出系统
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

}
