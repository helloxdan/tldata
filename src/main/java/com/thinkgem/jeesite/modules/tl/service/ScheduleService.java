package com.thinkgem.jeesite.modules.tl.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.plugins.xuser.IBot;
import org.telegram.plugins.xuser.XUtils;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.entity.TlUser;
import com.thinkgem.jeesite.modules.utils.Constants;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BotDataService botDataService;
	 
	@Autowired
	private JobService jobService;
	 
	@Autowired
	private BotService botService;
 

	Boolean run = null;

	public boolean isRun() {
		if (run == null) {
			run = Boolean.valueOf(Global.getConfig("tl.autoRun"));
			logger.info("是否启动调度程序，{}",run);
		}
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	/**
	 * 更新群组信息，总用户数，名称、title。
	 */
	// @Scheduled(cron = "0/10 * * * * ?")
	@Transactional(readOnly = false)
	public void scheduleUpdateGroupInfo() {
		// logger.info("定时调度，更新群组用户数量……");
		if(!isRun())
			return;
		// TODO
		Group group = new Group();
//		List<Group> list = groupService.findListWithoutUsernum(group);
//		int num = 0;
//		for (Group g : list) {
//			// 暂时只执行两次，检查api接口是否支持连续执行
//			if (num == 2)
//				break;
//			updateGroupInfo(g);
//			num++;
//		}
	}

	@Transactional(readOnly = false)
	public void updateGroupInfo(Group g) {
		// 通过管理员账号获取信息
		try {
			IBot bot = botService.getBotByPhone(botService.getAdminAccount(),
					true);

			JSONObject json = bot.getGroupInfo(Integer.parseInt(g.getId()),
					g.getAccesshash(), true);
			// String link = json.getString("link");
			Integer usernum = json.getInteger("usernum");
			if (usernum != null) {
//				Group group = groupService.get(g.getId());
//				// group.setUrl(link);
//				group.setUsernum(usernum);
//				group.preUpdate();
//				groupService.save(group);
			} else {
				logger.warn("通过账号{}无法获取群组【{}】详情", botService.getAdminAccount(),
						g.getName());

				if ("CHANNEL_PRIVATE".equals(json.getString("msg"))) {
					// remove
//					Group group = groupService.get(g.getId());
//					group.setOut("1");
//					group.setStatus("1");
//					groupService.save(group);
				}
			}

		} catch (Exception e) {
			logger.error("更新群组的link地址异常", e);
		}
	}

	// 采集用户的队列
	private Queue<Account> accountFetchQueue = new LinkedList<Account>();

	/**
	 * 定时抽取用户信息。
	 */
	// @Scheduled(cron = "0/5 * * * * ?")
	@Transactional(readOnly = false)
	public void scheduleFetchUser() {
		if(!isRun())
			return;
		// TODO
		// 1.查找用户数量少的账号，循环逐个处理
		// 2.找一个link url 不为空，索引偏移量少的群组
		// 3.执行抽取用户的操作
		Account account = new Account();
		List<Account> alist =null;// accountService.findUnfullUserAccount(account);
		if (accountFetchQueue.size() > 0) {
			// 待队列中执行完
			return;
		}
		if (alist.size() > 0)
			logger.info("定时调度，共有{}个账号需要从群组采集用户数据……", alist.size());
		for (Account a : alist) {
			if (!accountFetchQueue.contains(a)) {
				// 加入队列待处理
				accountFetchQueue.add(a);
			} else {
				logger.debug("{}账号已在待处理队列中，跳过", a.getId());
			}
		}

	}

	@Transactional(readOnly = false)
	public void handleAccountFetchUser() {

		Account a = accountFetchQueue.poll();
		if (a != null) {
			Group g = null;//groupService.getOneGroupForFetch();
			if (g == null) {
				// logger.warn("没有可抽取用户的群组");
				return;
			}

			if (StringUtils.isBlank(g.getUrl())) {
				logger.warn("群组{}没有邀请link", g.getName());
				return;
			}

			// 执行采集 操作
			fetchUserFromGroup(a.getId(), g);

			// 汇总下用户有效用户数
//			accountService.updateAccountData(a.getId());
		}
	}

	/**
	 * 从群组抽取数据。
	 * 
	 * @param id
	 * @param g
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void fetchUserFromGroup(String phone, Group g) {
		try {
			// 1.启动账号
			IBot bot = botService.getBotByPhone(phone, true);
			// 2.加入群组
			JSONObject result = bot.importInvite(g.getUrl());
			if (!result.getBooleanValue("success")) {
				// 加入群组失败
				logger.warn("{}加入群组{}失败", phone, g.getName());
				return;
			}
			int chatid = result.getIntValue("chatid");
			long accessHash = result.getLong("accessHash");
			// 3.抽取用户
			TLVector<TLAbsUser> users = bot.collectUsers(chatid, accessHash,
					g.getOffset(), Constants.FETCH_PAGE_SIZE);

			logger.info("拉取群组用户结果： account={},group={},size={}", phone,
					g.getName(), users.size());

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
								|| firstName.length() > 100 || (firstName
								.contains("拉人") || firstName.contains("电报群"))))) {
					logger.debug("用户名长度大于100，存在  拉人  电报群 字样，忽略");
					continue;
				}

				// if ("wojiaoshenmehao".equals(u.getUserName())) {
				// System.out.println("isBotCantAddToGroup="
				// + u.isBotCantAddToGroup());
				// }else{
				// System.out.println("isBotCantAddToGroup="
				// + u.isBotCantAddToGroup());
				// }

				JobUser ju = new JobUser();
				ju.setJobId("auto");
				ju.setAccount(phone);
				ju.setFromGroup(g.getId());
				ju.setUserid(u.getId() + "");
				ju.setUsername(u.getUserName());
				ju.setUserHash(u.getAccessHash());
				ju.setFirstname(firstName);
				ju.setLastname(lastName);
				ju.setStatus("0");
				// u.getLangCode();
				// u.getFirstName();
				// u.getLastName();
//				jobUserService.insertUserToJob(ju, "auto");

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
//				tlUserService.insertOrUpdate(tlu);

				num++;
			}

			// 修改群组的offset值
			g.setOffset(g.getOffset() + Constants.FETCH_PAGE_SIZE);
//			groupService.updateOffset(g);
		} catch (Exception e) {
			logger.error(phone + "-从群组" + g.getName() + "抽取用户异常,{}",
					e.getMessage());
		}
	}

	// @Scheduled(cron = "0 0/5 * * * ?")
	@Transactional(readOnly = false)
	public void scheduleJoinGroup() {
		logger.info("定时调度，账号自动加入有效群组……");

		// TODO
		// 1.找到
	}

}
