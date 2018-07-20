package org.telegram;

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.telegram.api.engine.RpcCallback;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesGetAllChats;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.IBotDataService;
import org.telegram.plugins.xuser.db.DefaultDatabaseManager;
import org.telegram.plugins.xuser.handler.ChatsHandler;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;
import org.telegram.plugins.xuser.handler.UsersHandler;
import org.telegram.plugins.xuser.support.BotConfigImpl;
import org.telegram.plugins.xuser.support.ChatUpdatesBuilderImpl;
import org.telegram.plugins.xuser.support.CustomUpdatesHandler;
import org.telegram.plugins.xuser.support.DefaultBotDataService;
import org.telegram.plugins.xuser.support.DifferenceParametersService;
import org.telegram.tl.TLObject;

import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class XUserMain {
	protected static org.slf4j.Logger logger = LoggerFactory
			.getLogger(XUserMain.class);
	private static final int APIKEY = 202491; // your api key
	private static final String APIHASH = "9f32d44fca581599dbbe02cec25ffe58"; // your
																				// api
																				// hash
	// private static final String PHONENUMBER = "8618566104318"; // Your phone
	// number
	private static final String PHONENUMBER = "8613427670753"; // Your phone
																// number

	// private static final int APIKEY = 208853; // your api key
	// private static final String APIHASH = "2b0149b45cfc13f5b272ad832608066b";
	// //
	// your api hash
	// private static final String PHONENUMBER = "8613751867473"; // Your phone
	// number
	public static void main(String[] args) {
		Logger.getGlobal().addHandler(new ConsoleHandler());
		Logger.getGlobal().setLevel(Level.ALL);

		LoginStatus status = null;
		String phone = PHONENUMBER;
		try {
			final BotConfig botConfig = new BotConfigImpl(phone);
			final IBotDataService botDataService = new DefaultBotDataService();
			// databaseManager.setBotConfig(botConfig);
			botDataService.setBotConfig(botConfig);
			final IUsersHandler usersHandler = new UsersHandler(botDataService);
			final IChatsHandler chatsHandler = new ChatsHandler(botDataService);
			final MessageHandler messageHandler = new MessageHandler();
			messageHandler.setBotConfig(botConfig);
			final TLMessageHandler tlMessageHandler = new TLMessageHandler(
					messageHandler, botDataService);

			final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(
					CustomUpdatesHandler.class);
			builder.setBotConfig(botConfig).setDatabaseManager(botDataService)
					.setUsersHandler(usersHandler)
					.setChatsHandler(chatsHandler)
					.setMessageHandler(messageHandler)
					.setTlMessageHandler(tlMessageHandler);

			logger.info("创建实例，api=[{}],apihash=[{}],phone={}", APIKEY, APIHASH,
					phone);
			TelegramBot kernel = new TelegramBot(botConfig, builder, APIKEY,
					APIHASH);
			// 覆盖默认的DifferenceParametersService
			DifferenceParametersService differenceParametersService = new DifferenceParametersService(
					botDataService);
			differenceParametersService.setAccount(phone);// 注入实例账号
			builder.setDifferenceParametersService(differenceParametersService);

			// 初始化，如果已经有登录过，就直接登录，返回登录成功的状态
			status = kernel.init();
			if (status == LoginStatus.CODESENT) {
				logger.warn(phone + "账号，已发送验证码，等待输入验证码");
				// setStatus(STATUS_WAIT);
			} else if (status == LoginStatus.ALREADYLOGGED) {
				kernel.startBot();
				// setStatus(STATUS_OK);
			} else {
				// setStatus(STATUS_FAIL);
				Log log = new Log("tl", phone + "，错误的登录状态");
				LogUtils.saveLog(log, null);
				throw new Exception("Failed to log in: " + status);
			}

		} catch (Exception e) {
			String msg = e.getMessage();
			if (StringUtils.isNotBlank(msg) && msg.contains("ERRORSENDINGCODE")) {
				logger.error("{}帐号登陆认证失败,重发验证码失败~~~~帐号失效~~~~~~~~``", phone);
			} else {
				logger.error("启动异常", e);
			}
			throw new RuntimeException("启动异常:" + e.getMessage());

		}
	}

	public static void main2(String[] args) {
		Logger.getGlobal().addHandler(new ConsoleHandler());
		Logger.getGlobal().setLevel(Level.ALL);

		final DefaultDatabaseManager databaseManager = new DefaultDatabaseManager();
		final BotConfig botConfig = new BotConfigImpl(PHONENUMBER);
		databaseManager.setBotConfig(botConfig);
		final IUsersHandler usersHandler = new UsersHandler(databaseManager);
		final IChatsHandler chatsHandler = new ChatsHandler(databaseManager);
		final MessageHandler messageHandler = new MessageHandler();
		messageHandler.setBotConfig(botConfig);
		final TLMessageHandler tlMessageHandler = new TLMessageHandler(
				messageHandler, databaseManager);

		final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(
				CustomUpdatesHandler.class);
		builder.setBotConfig(botConfig).setDatabaseManager(databaseManager)
				.setUsersHandler(usersHandler).setChatsHandler(chatsHandler)
				.setMessageHandler(messageHandler)
				.setTlMessageHandler(tlMessageHandler);

		try {
			final TelegramBot kernel = new TelegramBot(botConfig, builder,
					APIKEY, APIHASH);
			LoginStatus status = kernel.init();
			if (status == LoginStatus.CODESENT) {
				Scanner in = new Scanner(System.in);
				boolean success = kernel.getKernelAuth().setAuthCode(
						in.nextLine().trim());
				if (success) {
					status = LoginStatus.ALREADYLOGGED;
				}
			}
			if (status == LoginStatus.ALREADYLOGGED) {
				kernel.startBot();
			} else {
				throw new Exception("Failed to log in: " + status);
			}

			// 业务操作
			// doMyTask(kernel);

		} catch (Exception e) {
			BotLogger.severe("MAIN", e);
			e.printStackTrace();
		}
	}

	private static void doMyTask(TelegramBot kernel) {
		// TODO Auto-generated method stub
		TelegramApi api = kernel.getKernelComm().getApi();
		api.doRpcCall(new TLRequestMessagesGetAllChats(), new RpcCallback() {

			@Override
			public void onResult(TLObject result) {
				// TODO Auto-generated method stub
				System.out.println(result);
			}

			@Override
			public void onError(int errorCode, String message) {
				// TODO Auto-generated method stub

				System.out.println(errorCode + "," + message);
			}

		});
	}
}
