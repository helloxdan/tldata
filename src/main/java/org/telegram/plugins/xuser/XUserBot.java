package org.telegram.plugins.xuser;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.TLConfig;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.auth.sentcodetype.TLSentCodeTypeSms;
import org.telegram.api.channel.TLChannelParticipants;
import org.telegram.api.channel.participants.filters.TLChannelParticipantsFilterRecent;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelFull;
import org.telegram.api.contacts.TLContactsFound;
import org.telegram.api.contacts.TLResolvedPeer;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.channels.TLRequestChannelsGetFullChannel;
import org.telegram.api.functions.channels.TLRequestChannelsGetParticipants;
import org.telegram.api.functions.channels.TLRequestChannelsInviteToChannel;
import org.telegram.api.functions.channels.TLRequestChannelsJoinChannel;
import org.telegram.api.functions.contacts.TLRequestContactsResolveUsername;
import org.telegram.api.functions.contacts.TLRequestContactsSearch;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.messages.TLRequestMessagesGetFullChat;
import org.telegram.api.functions.messages.TLRequestMessagesImportChatInvite;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.user.TLAbsInputUser;
import org.telegram.api.input.user.TLInputUser;
import org.telegram.api.messages.TLMessagesChatFull;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.plugins.xuser.handler.ChatsHandler;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;
import org.telegram.plugins.xuser.handler.UsersHandler;
import org.telegram.plugins.xuser.support.BotConfigImpl;
import org.telegram.plugins.xuser.support.ChatUpdatesBuilderImpl;
import org.telegram.plugins.xuser.support.CustomUpdatesHandler;
import org.telegram.plugins.xuser.support.DefaultBotDataService;
import org.telegram.plugins.xuser.support.DifferenceParametersService;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.modules.sys.entity.Log;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.service.BotDataService;
import com.thinkgem.jeesite.modules.utils.Constants;

public class XUserBot implements IBot {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public static final String STATUS_READY = "READY";
	public static final String STATUS_WAIT = "WAIT";
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";

	private static final String LOGTAG = "XUserBot";

	private String status = STATUS_READY;

	private BotDataService botDataService;
	TelegramBot kernel = null;

	public IBotDataService getBotDataService(BotDataService botDataService) {
		// 一个封装实例，每个bot一个实例
		return new DefaultBotDataService(botDataService);
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
			final BotConfig botConfig = new BotConfigImpl(phone);
			final IBotDataService botDataService = getBotDataService(this.botDataService);
			// databaseManager.setBotConfig(botConfig);
			botDataService.setBotConfig(botConfig);
			final IUsersHandler usersHandler = new UsersHandler(botDataService);
			final IChatsHandler chatsHandler = new ChatsHandler(botDataService);
			final MessageHandler messageHandler = new MessageHandler();
			messageHandler.setBotConfig(botConfig);
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
	public JSONObject importInvite(String url) {
		logger.info("{}，join group {}", getAccount(), url);
		// kernel.getKernelComm().getApi().getState().isAuthenticated();
		boolean success = true;
		JSONObject json = new JSONObject();
		json.put("chatid", 0);

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

				json.put("chatid", ch.getChannelId());
				json.put("accessHash", ch.getAccessHash());

				logger.info("join channel result:{}", r);
			} catch (Exception e) {
				success = false;
				logger.error("入群失败", e);
			}
		}
		json.put("success", success);

		return json;
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
	public JSONObject getGroupInfo(int chatId, long chatAccessHash, boolean ischannel) {
		logger.info("{}，getGroupInfo ，{}", getAccount(), chatId);
		JSONObject json = new JSONObject();
		try {
			TelegramApi api = kernel.getKernelComm().getApi();
			if (ischannel) {
				TLRequestChannelsGetFullChannel req = new TLRequestChannelsGetFullChannel();
				TLInputChannel channel = new TLInputChannel();
				channel.setAccessHash(chatAccessHash);
				channel.setChannelId(chatId);
				req.setChannel(channel);
				TLMessagesChatFull result = api.doRpcCall(req);
				TLChannelFull ch = (TLChannelFull) result.getFullChat();
				TLVector<TLAbsChat> chats = result.getChats();
				if (chats.size() > 0) {
					TLChannel chat = (TLChannel) chats.get(0);
					json.put("title", chat.getTitle());
					json.put("username", chat.getUsername());
				}
				json.put("usernum", ch.getParticipantsCount());

				// 需要管理员权限

				/*
				 * TLRequestChannelsExportInvite req2=new TLRequestChannelsExportInvite();
				 * req2.setChannel(channel); TLAbsChatInvite r2 = api.doRpcCall(req2); if(r2
				 * instanceof TLChatInviteExported) { json.put("link",
				 * ((TLChatInviteExported)r2).getLink()); }
				 */

			} else {
				// TODO 未处理，少用到

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
					json.put("title", ch.getTitle());

				}
				// System.out.println("---");
				// System.out.println(result.getUsers().size());
				// TLVector<TLAbsUser> users = result.getUsers();
				// for (TLAbsUser uu : users) {
				// System.out.println(uu.getId());
				// }
			}
		} catch (IOException e) {
			logger.error("取群信息失败", e);
			// if private
			// FIXME
		} catch (TimeoutException e) {
			logger.error("取群信息失败，超时", e);
		}
		return json;
	}

	@Override
	public JSONObject searchUser(String username) {
		logger.info("searchUser {} in contact", username);
		JSONObject json = new JSONObject();
		try {
			TelegramApi api = kernel.getKernelComm().getApi();
			TLRequestContactsSearch req = new TLRequestContactsSearch();
			req.setQ(username);
			req.setLimit(1000);
			TLContactsFound found = api.doRpcCall(req);
			TLVector<TLAbsUser> users = found.getUsers();
			JSONObject ju = null;
			JSONArray au = new JSONArray();
			for (TLAbsUser user : users) {
				ju = new JSONObject();
				TLUser u = (TLUser) user;
				ju.put("id", u.getId());
				ju.put("username", u.getUserName());
				ju.put("accessHash", u.getAccessHash());
				ju.put("firstName", u.getFirstName());
				ju.put("lastName", u.getLastName());
				ju.put("phone", u.getPhone());
				au.add(ju);
			}
			json.put("users", au);
			JSONArray ac = new JSONArray();
			for (TLAbsChat chat : found.getChats()) {
				ju = new JSONObject();
				if (chat instanceof TLChannel) {
					TLChannel c = (TLChannel) chat;
					ju.put("id", c.getId());
					ju.put("username", c.getUsername());
					ju.put("accessHash", c.getAccessHash());
					ju.put("title", c.getTitle());
					ju.put("ischannel", true);
					ac.add(ju);
				}
			}
			json.put("chats", ac);

		} catch (Exception e) {
			logger.error("searchUser失败", e);
		}
		return json;
	}

	/**
	 * 账号注册。
	 * 
	 * @param phone
	 * @param apihash
	 * @param apikey
	 * @return
	 */
	@Override
	public JSONObject registe(String phone, int apikey, String apihash) {
		JSONObject data = new JSONObject();
		boolean result = false;
		String status = null;
		// chushihua
		if (apikey != -1) {
			startRegBot(phone, apikey, apihash);
		}
		data = registe();
		logger.info("send registe code result:{},status={}", data.getString("result"), data.getString("status"));
		/*
		 * final BotConfig config = kernel.getConfig();
		 * logger.info("Sending code to phone " + phone + "..."); TLSentCode sentCode =
		 * null; try { try { final TLRequestAuthSendCode tlRequestAuthSendCode =
		 * getSendCodeRequest(); sentCode =
		 * kernel.getKernelComm().getApi().doRpcCallNonAuth(tlRequestAuthSendCode); //
		 * createNextCodeTimer(sentCode.getTimeout());
		 * logger.info("sentCode,isPhoneRegistered={},type={},nextType={},timeout={}",
		 * sentCode.isPhoneRegistered(), sentCode.getType(), sentCode.getNextType(),
		 * sentCode.getTimeout()); // 判断是否为新手机号，没注册过的 if (!sentCode.isPhoneRegistered())
		 * { // 未注册过 status = "未注册"; config.setRegistered(true); result = true; //
		 * 进入等待状态，待输入验证码 } else { // 已注册，写入黑名单 result = false; status = "黑名单"; } } catch
		 * (RpcException e) { logger.error("注册接口调用失败", e); result = false; } catch
		 * (TimeoutException e) { logger.error("注册发送信息接口调用超时", e); sentCode = null;
		 * result = false; } if (sentCode != null) {
		 * config.setHashCode(sentCode.getPhoneCodeHash());
		 * config.setRegistered(sentCode.isPhoneRegistered());
		 * logger.info("sent Code to {}", phone); // data.put("status",
		 * LoginStatus.CODESENT); status = LoginStatus.CODESENT.name(); } else { //
		 * data.put("status", LoginStatus.ERRORSENDINGCODE); status =
		 * LoginStatus.ERRORSENDINGCODE.name(); result = false; } } catch (Exception e)
		 * { logger.error("registe Account failure", phone); // result =
		 * LoginStatus.UNEXPECTEDERROR; result = false; }
		 */
		// 记录结果
		// data.put("result", result);
		// data.put("status", status);
		return data;
	}

	private TLRequestAuthSendCode getSendCodeRequest() {
		BotConfig config = kernel.getConfig();
		final TLRequestAuthSendCode tlRequestAuthSendCode = new TLRequestAuthSendCode();
		tlRequestAuthSendCode.setPhoneNumber(config.getPhoneNumber());
		tlRequestAuthSendCode.setApiId(Constants.APIKEY);
		tlRequestAuthSendCode.setApiHash(Constants.APIHASH);
		// tlRequestAuthSendCode.set
		return tlRequestAuthSendCode;
	}

	public LoginStatus startRegBot(String phone, int apikey, String apihash) {
		LoginStatus status = null;
		try {
			final BotConfig botConfig = new BotConfigImpl(phone);
			final IBotDataService botDataService = getBotDataService(this.botDataService);
			// databaseManager.setBotConfig(botConfig);
			botDataService.setBotConfig(botConfig);
			final IUsersHandler usersHandler = new UsersHandler(botDataService);
			final IChatsHandler chatsHandler = new ChatsHandler(botDataService);
			final MessageHandler messageHandler = new MessageHandler();
			messageHandler.setBotConfig(botConfig);
			final TLMessageHandler tlMessageHandler = new TLMessageHandler(messageHandler, botDataService);

			final ChatUpdatesBuilderImpl builder = new ChatUpdatesBuilderImpl(CustomUpdatesHandler.class);
			builder.setBotConfig(botConfig).setDatabaseManager(botDataService).setUsersHandler(usersHandler)
					.setChatsHandler(chatsHandler).setMessageHandler(messageHandler)
					.setTlMessageHandler(tlMessageHandler);

			logger.info("创建实例，" + phone);
			kernel = new RegTelegramBot(botConfig, builder, apikey, apihash);
			// 覆盖默认的DifferenceParametersService
			DifferenceParametersService differenceParametersService = new DifferenceParametersService(botDataService);
			differenceParametersService.setAccount(getAccount());// 注入实例账号
			builder.setDifferenceParametersService(differenceParametersService);

			status = kernel.init();
		} catch (Exception e) {
			logger.error("启动异常", e);
			throw new RuntimeException("启动异常:" + e.getMessage());
		}
		return status;
	}

	public JSONObject registe() {
		JSONObject json = new JSONObject();
		LoginStatus result;
		IKernelComm kernelComm = kernel.getKernelComm();
		try {
			if (getApiState().isAuthenticated()) {
				// BotLogger.info(LOGTAG, "Found Auth file");
				BotConfig config = kernel.getConfig();
				config.setRegistered(true);
				result = LoginStatus.ALREADYLOGGED;
			} else {
				try {
					final TLConfig config = kernelComm.getApi().doRpcCallNonAuth(new TLRequestHelpGetConfig());
					BotLogger.info(LOGTAG, "Loaded DC list");
					getApiState().updateSettings(config);
				} catch (IOException | TimeoutException e) {
					// BotLogger.error(LOGTAG, e);
					logger.warn("send getconfig request timeout ");
				}
				BotConfig config = kernel.getConfig();
				BotLogger.info(LOGTAG, "Sending code to phone " + config.getPhoneNumber() + "...");
				TLSentCode sentCode = null;
				RegKernelAuth kernelAuth = (RegKernelAuth) kernel.getKernelAuth();
				try {
					final TLRequestAuthSendCode tlRequestAuthSendCode = getSendCodeRequest();
					sentCode = kernelComm.getApi().doRpcCallNonAuth(tlRequestAuthSendCode);
					kernelAuth.createNextCodeTimer(sentCode.getTimeout());

				} catch (RpcException e) {
					if (e.getErrorCode() == RegKernelAuth.ERROR303) {
						final int destDC = kernelAuth.updateDCWhenLogin(e);
						if (destDC != -1) {
							getApiState().setPrimaryDc(destDC);
							kernelComm.getApi().switchToDc(destDC);
							sentCode = retryRegiste(destDC);
						}
					}
				} catch (TimeoutException e) {
					// BotLogger.error(LOGTAG, e);
					logger.warn("send code request timeout ");
					sentCode = null;
					result = LoginStatus.ERRORSENDINGCODE;
					json.put("result", "timeout");
					json.put("status", LoginStatus.ERRORSENDINGCODE.toString());
				}

				if (sentCode != null) {
					config.setHashCode(sentCode.getPhoneCodeHash());
					// 标记是否已经注册
					// config.setRegistered(sentCode.isPhoneRegistered());
					BotLogger.info(LOGTAG, "sent Code");
					result = LoginStatus.CODESENT;
					// if send sms
					if (sentCode.getType() instanceof TLSentCodeTypeSms) {
						logger.info("success send sms code");
						json.put("result", "success");
						json.put("status", result.toString());
					} else {
						// set error flag
						json.put("result", "failure");
						json.put("status", sentCode.getType().toString());
					}
				} else {
					json.put("result", "sentCode is null");
					result = LoginStatus.ERRORSENDINGCODE;
					json.put("status", result.toString());
				}

			}
		} catch (IOException ex) {
			BotLogger.error(LOGTAG, ex);
			result = LoginStatus.UNEXPECTEDERROR;
			json.put("result", "ioerror");
			json.put("status", "ioerror");
		} catch (TimeoutException ex) {
			// timeout
			json.put("result", "timeout");
			json.put("status", LoginStatus.ERRORSENDINGCODE);
		}

		return json;
	}

	private AbsApiState getApiState() {
		return kernel.getKernelAuth().getApiState();
	}

	private TLSentCode retryRegiste(int destDC) throws IOException, TimeoutException {
		final TLSentCode sentCode;
		kernel.getKernelComm().getApi().switchToDc(destDC);
		final TLRequestAuthSendCode tlRequestAuthSendCode = getSendCodeRequest();
		sentCode = kernel.getKernelComm().getApi().doRpcCallNonAuth(tlRequestAuthSendCode);
		RegKernelAuth kernelAuth = (RegKernelAuth) kernel.getKernelAuth();
		kernelAuth.resetTimer();
		kernelAuth.createNextCodeTimer(sentCode.getTimeout());
		return sentCode;
	}

	@Override
	public JSONObject setRegAuthCode(String phone, String code) {
		logger.info("setRegAuthCode，" + getAccount() + ",code=" + code);
		RegKernelAuth kernelAuth = (RegKernelAuth) kernel.getKernelAuth();
		JSONObject json = kernelAuth.setRegAuthCode(code);
		if (!json.getBooleanValue("result")) {
			logger.error("{}，注册验证码{}校验失败,type={},msg={}", phone, code,json.getString("type"),json.getString("msg"));
		}
		return json;
	}
}
