package org.telegram.plugins.xuser;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.channel.TLChannelParticipants;
import org.telegram.api.channel.participants.filters.TLChannelParticipantsFilterRecent;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.contacts.TLResolvedPeer;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.channels.TLRequestChannelsGetParticipants;
import org.telegram.api.functions.channels.TLRequestChannelsInviteToChannel;
import org.telegram.api.functions.channels.TLRequestChannelsJoinChannel;
import org.telegram.api.functions.contacts.TLRequestContactsResolveUsername;
import org.telegram.api.functions.messages.TLRequestMessagesGetFullChat;
import org.telegram.api.functions.messages.TLRequestMessagesImportChatInvite;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.user.TLAbsInputUser;
import org.telegram.api.input.user.TLInputUser;
import org.telegram.api.messages.TLMessagesChatFull;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.handler.ChatsHandler;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;
import org.telegram.plugins.xuser.handler.UsersHandler;
import org.telegram.plugins.xuser.support.BotConfigImpl;
import org.telegram.plugins.xuser.support.ChatUpdatesBuilderImpl;
import org.telegram.plugins.xuser.support.CustomUpdatesHandler;
import org.telegram.plugins.xuser.support.DifferenceParametersService;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.service.BotDataService;

public class XUserBot implements IBot {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public static final String STATUS_READY = "READY";
	public static final String STATUS_WAIT = "WAIT";
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";

	private String status = STATUS_READY;

	private BotDataService botDataService;
	TelegramBot kernel = null;

	public BotDataService getBotDataService() {
		return botDataService;
	}

	public void setBotDataService(BotDataService botDataService) {
		this.botDataService = botDataService;
	}

	public TelegramBot getKernel() {
		return kernel;
	}

	@Override
	public LoginStatus start(String phone, int apikey, String apihash) {
		LoginStatus status = null;
		try {
			final IBotDataService botDataService = getBotDataService();
			final BotConfig botConfig = new BotConfigImpl(phone);
			// databaseManager.setBotConfig(botConfig);
			botDataService.setBotConfig(botConfig);
			final IUsersHandler usersHandler = new UsersHandler(botDataService);
			final IChatsHandler chatsHandler = new ChatsHandler(botDataService);
			final MessageHandler messageHandler = new MessageHandler();
			final TLMessageHandler tlMessageHandler = new TLMessageHandler(messageHandler, botDataService);

			final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(CustomUpdatesHandler.class);
			builder.setBotConfig(botConfig).setDatabaseManager(botDataService).setUsersHandler(usersHandler)
					.setChatsHandler(chatsHandler).setMessageHandler(messageHandler)
					.setTlMessageHandler(tlMessageHandler);

			logger.info("创建实例，" + phone);
			kernel = new TelegramBot(botConfig, builder, apikey, apihash);
			// 覆盖默认的DifferenceParametersService
			DifferenceParametersService differenceParametersService = new DifferenceParametersService(botDataService);
			differenceParametersService.setAccount(getAccount());// 注入实例账号
			builder.setDifferenceParametersService(differenceParametersService);

			// 初始化，如果已经有登录过，就直接登录，返回登录成功的状态
			status = kernel.init();
			if (status == LoginStatus.CODESENT) {
				logger.warn(phone + "账号，已发送验证码，等待输入验证码");
				setStatus(STATUS_WAIT);
			} else if (status == LoginStatus.ALREADYLOGGED) {
				kernel.startBot();
				setStatus(STATUS_OK);
			} else {
				setStatus(STATUS_FAIL);
				Log log = new Log("tl", getAccount() + "，错误的登录状态");
				LogUtils.saveLog(log, null);
				throw new Exception("Failed to log in: " + status);
			}
		} catch (Exception e) {
			logger.error("启动异常", e);
			throw new RuntimeException("启动异常:" + e.getMessage());
		}
		return status;
	}

	@Override
	public JSONObject getState() {
		logger.info("getState，" + getAccount());
		boolean isAuthenticated = kernel.getKernelComm().getApi().getState().isAuthenticated();
		JSONObject json = new JSONObject();
		json.put("isAuthenticated", isAuthenticated);
		json.put("isRunning", kernel.getMainHandler().isRunning());
		return json;
	}

	@Override
	public boolean setAuthCode(String phone, String code) {
		logger.info("setAuthCode，" + getAccount() + ",code=" + code);
		boolean success = kernel.getKernelAuth().setAuthCode(code);
		if (!success) {
			logger.error("{}，验证码{}校验失败", phone, code);
		}
		return success;
	}

	/**
	 * 加入群组。
	 * 
	 * @see org.telegram.plugins.xuser.IBot#importInvite(java.lang.String)
	 */
	@Override
	public boolean importInvite(String url) {
		logger.info("{}，join group {}", getAccount(), url);
		// kernel.getKernelComm().getApi().getState().isAuthenticated();
		boolean success = true;

		// TODO 加入群组
		IKernelComm kernelComm = kernel.getKernelComm();
		if (url.contains("t.me/joinchat")) {
			String hash = url.split("/")[(url.split("/").length) - 1];
			TLRequestMessagesImportChatInvite in = new TLRequestMessagesImportChatInvite();
			in.setHash(hash);
			try {
				TLAbsUpdates bb = kernelComm.getApi().doRpcCall(in);

				logger.info("入群结果：" + bb);
			} catch (Exception e) {
				success = false;
				logger.error("入群失败", e);
			}
		} else if (url.contains("t.me/")) {
			String username = url.split("/")[(url.split("/").length) - 1];
			try {
				TLRequestContactsResolveUsername ru = new TLRequestContactsResolveUsername();
				ru.setUsername(username);
				TLResolvedPeer peer = kernelComm.getApi().doRpcCall(ru);
				TLRequestChannelsJoinChannel join = new TLRequestChannelsJoinChannel();
				TLInputChannel ch = new TLInputChannel();
				ch.setChannelId(peer.getChats().get(0).getId());
				ch.setAccessHash(((TLChannel) peer.getChats().get(0)).getAccessHash());
				join.setChannel(ch);
				TLAbsUpdates r = kernelComm.getApi().doRpcCall(join);
				logger.info("join channel result:{}", r);
			} catch (Exception e) {
				success = false;
				logger.error("入群失败", e);
			}
		}

		return success;
	}

	public boolean importInvite2(String hash) {
		logger.info("{}，join group {}", getAccount(), hash);
		// kernel.getKernelComm().getApi().getState().isAuthenticated();
		boolean success = true;

		// TODO 加入群组
		try {
			TLRequestMessagesImportChatInvite req = new TLRequestMessagesImportChatInvite();
			req.setHash(hash);
			TLAbsUpdates result = kernel.getKernelComm().getApi().doRpcCall(req);
			logger.info("入群结果：" + result);
		} catch (IOException e) {
			success = false;
			logger.error("入群失败", e);
		} catch (TimeoutException e) {
			success = false;
			logger.error("入群失败，超时", e);
		}

		return success;
	}

	/**
	 * 当前账号。
	 * 
	 * @return
	 */
	private String getAccount() {
		return kernel.getConfig().getPhoneNumber();
	}

	/**
	 * 收集用户信息。
	 * 
	 * @see org.telegram.plugins.xuser.IBot#collectUsers(java.lang.String)
	 */
	@Override
	public TLVector<TLAbsUser> collectUsers(int chatId, long accessHash, int offset, int limit) {
		TLVector<TLAbsUser> users = null;
		logger.info("collectUsers from group ，" + chatId);
		try {
			TelegramApi api = kernel.getKernelComm().getApi();
			TLRequestChannelsGetParticipants req = new TLRequestChannelsGetParticipants();
			TLInputChannel c = new TLInputChannel();
			c.setChannelId(chatId);
			c.setAccessHash(accessHash);
			req.setChannel(c);
			req.setOffset(offset);
			req.setLimit(limit);
			req.setFilter(new TLChannelParticipantsFilterRecent());

			TLChannelParticipants result = api.doRpcCall(req);
			// 最近用户列表
			users = result.getUsers();

		} catch (IOException e) {
			logger.error("拉取群用户失败", e);
		} catch (TimeoutException e) {
			logger.error("拉取群用户失败，超时", e);
		}
		return users;
	}

	/**
	 * 需要从数据库获取加入用户数据，并加入群组。
	 * 
	 * @see org.telegram.plugins.xuser.IBot#addUsers(java.lang.String)
	 */
	@Override
	public void addUsers(int chatId, long accessHash, List<JobUser> jobUsers) {
		// TODO Auto-generated method stub
		logger.info("{},addUsers to group {}", getAccount(), chatId);
		try {
			TelegramApi api = kernel.getKernelComm().getApi();
			TLRequestChannelsInviteToChannel req = new TLRequestChannelsInviteToChannel();
			TLInputChannel ch = new TLInputChannel();
			ch.setChannelId(chatId);
			ch.setAccessHash(accessHash);
			req.setChannel(ch);

			TLVector<TLAbsInputUser> users = new TLVector<TLAbsInputUser>();
			for (JobUser jobUser : jobUsers) {
				TLInputUser user = new TLInputUser();
				user.setUserId(Integer.parseInt(jobUser.getUserid()));
				user.setAccessHash(jobUser.getUserHash());
				users.add(user);
			}
			req.setUsers(users);
			TLAbsUpdates result = api.doRpcCall(req);
			logger.info("拉人结果：" + result);
		} catch (IOException e) {
			logger.error("拉取群用户失败", e);
		} catch (TimeoutException e) {
			logger.error("拉取群用户失败，超时", e);
		}
	}

	@Override
	public boolean stop() {
		kernel.stopBot();
		// TODO 停止
		return true;
	}

	@Override
	public boolean setAdmin(int chatId, int userId, boolean isAdmin) {
		// TODO 设置管理员
		return false;
	}

	/**
	 * 实例状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	@Override
	public void getGroupInfo(int chatId, long chatAccessHash) {
		logger.info("{}，getGroupInfo ，{}", getAccount(), chatId);
		try {
			TelegramApi api = kernel.getKernelComm().getApi();
			TLRequestMessagesGetFullChat req = new TLRequestMessagesGetFullChat();
			req.setChatId(chatId);

			TLMessagesChatFull result = api.doRpcCall(req);
			TLVector<TLAbsChat> chats = result.getChats();
			for (TLAbsChat cc : chats) {
				TLChat ch = (TLChat) cc;
				System.out.println(ch.getTitle());
				System.out.println(ch.getParticipantsCount());
				System.out.println(ch.getVersion());
				System.out.println(ch.getClassId());

			}
			System.out.println("---");
			System.out.println(result.getUsers().size());
			TLVector<TLAbsUser> users = result.getUsers();
			for (TLAbsUser uu : users) {
				System.out.println(uu.getId());
			}

		} catch (IOException e) {
			logger.error("取群信息失败", e);
		} catch (TimeoutException e) {
			logger.error("取群信息失败，超时", e);
		}
	}
}
