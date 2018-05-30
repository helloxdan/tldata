package org.telegram.plugins.xuser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.TelegramApi;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.db.DefaultDatabaseManager;
import org.telegram.plugins.xuser.handler.ChatsHandler;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;
import org.telegram.plugins.xuser.handler.UsersHandler;
import org.telegram.plugins.xuser.support.BotConfigImpl;
import org.telegram.plugins.xuser.support.ChatUpdatesBuilderImpl;
import org.telegram.plugins.xuser.support.CustomUpdatesHandler;

import com.alibaba.fastjson.JSONObject;

public class XUserBot implements IBot {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	TelegramBot kernel = null;

	@Override
	public LoginStatus start(String phone, int apikey, String apihash) {
		LoginStatus status = null;
		try {
			final DefaultDatabaseManager databaseManager = new DefaultDatabaseManager();
			final BotConfig botConfig = new BotConfigImpl(phone);
			databaseManager.setBotConfig(botConfig);
			final IUsersHandler usersHandler = new UsersHandler(databaseManager);
			final IChatsHandler chatsHandler = new ChatsHandler(databaseManager);
			final MessageHandler messageHandler = new MessageHandler();
			final TLMessageHandler tlMessageHandler = new TLMessageHandler(
					messageHandler, databaseManager);

			final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(
					CustomUpdatesHandler.class);
			builder.setBotConfig(botConfig).setDatabaseManager(databaseManager)
					.setUsersHandler(usersHandler)
					.setChatsHandler(chatsHandler)
					.setMessageHandler(messageHandler)
					.setTlMessageHandler(tlMessageHandler);

			logger.info("创建实例，" + phone);
			kernel = new TelegramBot(botConfig, builder, apikey, apihash);
			status = kernel.init();
		} catch (Exception e) {
			logger.error("启动异常", e);
			throw new RuntimeException("启动异常:" + e.getMessage());
		}
		return status;
	}

	@Override
	public JSONObject getState() {
		logger.info("getState，" + kernel.getConfig().getPhoneNumber());
		boolean isAuthenticated = kernel.getKernelComm().getApi().getState()
				.isAuthenticated();
		JSONObject json = new JSONObject();
		json.put("isAuthenticated", isAuthenticated);
		json.put("isRunning", kernel.getMainHandler().isRunning());
		return json;
	}

	@Override
	public boolean setAuthCode(String phone, String code) {
		logger.info("setAuthCode，" + kernel.getConfig().getPhoneNumber()
				+ ",code=" + code);
		boolean success = kernel.getKernelAuth().setAuthCode(code);
		return success;
	}

	@Override
	public boolean importInvite(String url) {
		logger.info("join group ，" + url);
		kernel.getKernelComm().getApi().getState().isAuthenticated();
		return false;
	}

	@Override
	public void collectUsers(String chatId) {
		logger.info("collectUsers from group ，" + chatId);
		TelegramApi api = kernel.getKernelComm().getApi();

	}

	/**
	 * 需要从数据库获取加入用户数据，并加入群组。
	 * 
	 * @see org.telegram.plugins.xuser.IBot#addUsers(java.lang.String)
	 */
	@Override
	public void addUsers(String chatId) {
		// TODO Auto-generated method stub
		logger.info("addUsers to group ，" + chatId);
	}

	@Override
	public boolean stop(String phone) {
		kernel.stopBot();
		return true;
	}

	@Override
	public boolean setAdmin(String chatId, String userId, boolean isAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

}
