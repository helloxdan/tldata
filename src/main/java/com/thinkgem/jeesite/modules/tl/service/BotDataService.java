package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.entity.ChatImpl;
import org.telegram.plugins.xuser.entity.User;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.modules.tl.entity.DifferencesData;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.entity.TlUser;
import com.thinkgem.jeesite.modules.tl.entity.UserSession;

@Service
@Transactional(readOnly = true)
public class BotDataService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ChatService chatService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private DifferencesDataService differencesDataService;
	@Autowired
	private TlUserService tlUserService;

	public Chat getChatById(String phone, int chatId) {
		return chatService.getChatById(phone, chatId);

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean updateChat(String phone, ChatImpl cc) {
		try {
			com.thinkgem.jeesite.modules.tl.entity.Chat chat = new com.thinkgem.jeesite.modules.tl.entity.Chat();
			chat.setIsNewRecord(false);
			chat.setId(phone + cc.getId());
			chat.setAccount(phone);
			chat.setAccesshash(cc.getAccessHash());
			chat.setChatid(cc.getId() + "");
			chat.setIsChannel(cc.isChannel() ? 1 : 0);
			chat.setTitle(cc.getTitle());
			chatService.save(chat);

			// 增加群组表
			Group group = new Group();
			group.setId(cc.getId() + "");
			group.setName(cc.getTitle());
			group.setUsername(cc.getUsername());
			group.setIsChannel(cc.isChannel() ? "1" : "0");
			group.setStatus(Global.NO);
			group.setUpcateDate(new Date());
			groupService.insertOrUpdate(group);
		} catch (Exception e) {
			logger.error("updateChat", e);
		}
		return true;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean addChat(String phone, ChatImpl cc) {
		try {
			com.thinkgem.jeesite.modules.tl.entity.Chat chat = new com.thinkgem.jeesite.modules.tl.entity.Chat();
			chat.setIsNewRecord(true);
			chat.setId(phone + cc.getId());
			chat.setAccount(phone);
			chat.setAccesshash(cc.getAccessHash());
			chat.setChatid(cc.getId() + "");
			chat.setIsChannel(cc.isChannel() ? 1 : 0);
			chat.setTitle(cc.getTitle());
			chatService.save(chat);

			// 增加群组表
			Group group = new Group();
			group.setId(cc.getId() + "");
			group.setName(cc.getTitle());
			group.setUsername(cc.getUsername());
			group.setIsChannel(cc.isChannel() ? "1" : "0");
			group.setStatus(Global.NO);
			group.setUpcateDate(new Date());
			groupService.insertOrUpdate(group);
		} catch (Exception e) {
			logger.error("addChat", e);
		}
		return true;
	}

	public @Nullable IUser getUserById(String phone, int userId) {
		return userSessionService.getUserById(phone, userId);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean addUser(String phone, User user) {
		UserSession us = new UserSession();
		us.setIsNewRecord(true);
		us.setId(phone + user.getUserId());
		us.setAccount(phone);
		us.setUserid(user.getUserId());
		us.setUserhash(user.getUserHash());
		us.setUsername(user.getUsername());
		userSessionService.save(us);

		// 记录到用户表
		TlUser tlUser = new TlUser();
		tlUser.setIsNewRecord(true);
		tlUser.preInsert();
		tlUser.setId(user.getUserId() + "");
		tlUser.setUsername(user.getUsername());
		tlUser.setFirstname(user.getFirstName());
		tlUser.setLastname(user.getLastName());
		tlUser.setLangcode(user.getLangCode());
		tlUser.setMsgTime(new Date());
		tlUserService.save(tlUser);

		return true;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean updateUser(String phone, User user) {
		UserSession us = new UserSession();
		us.setIsNewRecord(false);
		us.setId(phone + user.getUserId());
		us.setAccount(phone);
		us.setUserid(user.getUserId());
		us.setUserhash(user.getUserHash());
		us.setUsername(user.getUsername());
		userSessionService.save(us);
		
		TlUser tlUser = new TlUser();
		tlUser.setId(user.getUserId() + "");
		tlUserService.updateMsgNum(tlUser);
		return true;
	}

	public List<User> findUsers() {
		UserSession userSession = new UserSession();
		Page<UserSession> page = new Page<UserSession>();
		// 只查询前100条记录
		page.setPageSize(100);
		userSession.setPage(page);
		List<UserSession> list = userSessionService.findList(userSession);
		List<User> users = new ArrayList<User>();
		for (UserSession user : list) {
			User u = new User(user.getUserid());
			u.setAccount(user.getAccount());
			u.setUserHash(user.getUserhash());
			u.setUsername(user.getUsername());
			users.add(u);
		}
		return users;

	}

	public @NotNull Map<Integer, int[]> getDifferencesData(String phone) {
		return differencesDataService.getDifferencesData(phone);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public boolean updateDifferencesData(String phone, int botId, int pts,
			int date, int seq) {
		// 检查是否存在记录，没有则新增，否则更新
		try {
			String id = phone + botId;
			DifferencesData diff = differencesDataService.get(id);
			if (diff == null) {
				diff = new DifferencesData();
				diff.setIsNewRecord(true);
				diff.setId(id);
				diff.setAccount(phone);
				diff.setBotid(botId);
				diff.setPts(pts);
				diff.setDate(date);
				diff.setSeq(seq);
				diff.setUpdateDate(new Date());
			} else {
				diff.setIsNewRecord(false);
				diff.setBotid(botId);
				diff.setAccount(phone);
				diff.setPts(pts);
				diff.setDate(date);
				diff.setSeq(seq);
				diff.setUpdateDate(new Date());
			}
			differencesDataService.save(diff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
