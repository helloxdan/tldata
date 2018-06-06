package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.IBotDataService;
import org.telegram.plugins.xuser.entity.ChatImpl;
import org.telegram.plugins.xuser.entity.User;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.modules.tl.entity.DifferencesData;
import com.thinkgem.jeesite.modules.tl.entity.UserSession;

@Service
@Transactional(readOnly = true)
public class BotDataService implements IBotDataService {
	@Autowired
	private ChatService chatService;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private DifferencesDataService differencesDataService;

	BotConfig botConfig = null;

	public BotConfig getBotConfig() {
		return botConfig;
	}

	private String getPhone() {
		return getBotConfig().getPhoneNumber();
	}

	@Override
	public @Nullable Chat getChatById(int chatId) {
		return chatService.getChatById(getPhone(), chatId);

	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateChat(ChatImpl cc) {
		com.thinkgem.jeesite.modules.tl.entity.Chat chat = new com.thinkgem.jeesite.modules.tl.entity.Chat();
		chat.setIsNewRecord(false);
		chat.setId(getPhone() + cc.getId());
		chat.setAccount(getPhone());
		chat.setAccesshash(cc.getAccessHash());
		chat.setChatid(cc.getChatid() + "");
		chat.setIsChannel(cc.isChannel() ? 1 : 0);
		chat.setTitle(cc.getTitle());
		chatService.save(chat);
		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean addChat(ChatImpl cc) {
		com.thinkgem.jeesite.modules.tl.entity.Chat chat = new com.thinkgem.jeesite.modules.tl.entity.Chat();
		chat.setIsNewRecord(true);
		chat.setId(getPhone() + cc.getId());
		chat.setAccount(getPhone());
		chat.setAccesshash(cc.getAccessHash());
		chat.setChatid(cc.getChatid() + "");
		chat.setIsChannel(cc.isChannel() ? 1 : 0);
		chat.setTitle(cc.getTitle());
		chatService.save(chat);
		return true;
	}

	@Override
	public @Nullable IUser getUserById(int userId) {
		return userSessionService.getUserById(getPhone(), userId);
	}

	@Transactional(readOnly = false)
	public boolean addUser(User user) {
		UserSession us = new UserSession();
		us.setIsNewRecord(true);
		us.setId(getPhone() + user.getUserId());
		us.setAccount(getPhone());
		us.setUserid(user.getUserId());
		us.setUserhash(user.getUserHash());
		us.setUsername(user.getUsername());
		userSessionService.save(us);
		return true;
	}

	@Transactional(readOnly = false)
	public boolean updateUser(User user) {
		UserSession us = new UserSession();
		us.setIsNewRecord(false);
		us.setId(getPhone() + user.getUserId());
		us.setAccount(getPhone());
		us.setUserid(user.getUserId());
		us.setUserhash(user.getUserHash());
		us.setUsername(user.getUsername());
		userSessionService.save(us);
		return true;
	}

	@Override
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

	@Override
	public @NotNull Map<Integer, int[]> getDifferencesData() {
		return differencesDataService.getDifferencesData(getPhone());
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
		// 检查是否存在记录，没有则新增，否则更新
		String id = getPhone() + botId;
		DifferencesData diff = differencesDataService.get(id);
		if (diff == null) {
			diff = new DifferencesData();
			diff.setIsNewRecord(true);
			diff.setId(id);
			diff.setBotid(botId);
			diff.setPts(pts);
			diff.setDate(date);
			diff.setSeq(seq);
		} else {
			diff.setIsNewRecord(false);
			diff.setBotid(botId);
			diff.setPts(pts);
			diff.setDate(date);
			diff.setSeq(seq);
		}
		differencesDataService.save(diff);
		return false;
	}

	@Override
	public void setBotConfig(BotConfig botConfig) {
		this.botConfig = botConfig;
	}

}
