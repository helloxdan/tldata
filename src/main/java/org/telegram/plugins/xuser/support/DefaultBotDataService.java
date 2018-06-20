package org.telegram.plugins.xuser.support;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.IBotDataService;
import org.telegram.plugins.xuser.entity.ChatImpl;
import org.telegram.plugins.xuser.entity.User;

import com.thinkgem.jeesite.modules.tl.service.BotDataService;

public class DefaultBotDataService implements IBotDataService {
	BotConfig botConfig = null;
	BotDataService botDataService = null;

	public DefaultBotDataService() {
	}

	public DefaultBotDataService(BotDataService botDataService) {
		this.botDataService = botDataService;
	}

	public BotConfig getBotConfig() {
		return botConfig;
	}

	private String getPhone() {
		return getBotConfig().getPhoneNumber();
	}

	public BotDataService getBotDataService() {
		return botDataService;
	}

	@Override
	public @Nullable Chat getChatById(int chatId) {
		return getBotDataService().getChatById(getPhone(), chatId);
	}

	@Override
	public @Nullable IUser getUserById(int userId) {
		return getBotDataService().getUserById(getPhone(), userId);
	}

	@Override
	public @NotNull Map<Integer, int[]> getDifferencesData() {
		return getBotDataService().getDifferencesData(getPhone());
	}

	@Override
	public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
		return getBotDataService().updateDifferencesData(getPhone(), botId,
				pts, date, seq);
	}

	@Override
	public void setBotConfig(BotConfig botConfig) {
		this.botConfig = botConfig;
	}

	@Override
	public boolean addUser(User user) {
		return getBotDataService().addUser(getPhone(), user);
	}

	@Override
	public boolean updateUser(User user) {
		return getBotDataService().updateUser(getPhone(), user);
	}

	@Override
	public boolean updateChat(ChatImpl current) {
		return getBotDataService().updateChat(getPhone(), current);
	}

	@Override
	public boolean addChat(ChatImpl current) {
		return getBotDataService().addChat(getPhone(), current);
	}

	@Override
	public List<User> findUsers() {
		return getBotDataService().findUsers();
	}

}
