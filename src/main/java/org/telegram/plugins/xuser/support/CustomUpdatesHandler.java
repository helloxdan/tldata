package org.telegram.plugins.xuser.support;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.update.TLUpdateUserStatus;
import org.telegram.api.updates.TLUpdateShortChatMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.handler.MessageHandler;
import org.telegram.plugins.xuser.handler.TLMessageHandler;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class CustomUpdatesHandler extends DefaultUpdatesHandler {
	private static final String LOGTAG = "CHATUPDATESHANDLER";
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private final DatabaseManager databaseManager;
	private BotConfig botConfig;
	private MessageHandler messageHandler;
	private IUsersHandler usersHandler;
	private IChatsHandler chatsHandler;
	private TLMessageHandler tlMessageHandler;

	public CustomUpdatesHandler(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService,
			DatabaseManager databaseManager) {
		super(kernelComm, differenceParametersService, databaseManager);
		this.databaseManager = databaseManager;
	}

	public void setConfig(BotConfig botConfig) {
		this.botConfig = botConfig;
	}

	public void setHandlers(MessageHandler messageHandler, IUsersHandler usersHandler, IChatsHandler chatsHandler,
			TLMessageHandler tlMessageHandler) {
		this.messageHandler = messageHandler;
		this.chatsHandler = chatsHandler;
		this.usersHandler = usersHandler;
		this.tlMessageHandler = tlMessageHandler;
	}

	/**
	 * 用户消息 message .
	 * 
	 * @see org.telegram.bot.handlers.DefaultUpdatesHandler#onTLUpdateShortMessageCustom(org.telegram.api.updates.TLUpdateShortMessage)
	 */
	@Override
	public void onTLUpdateShortMessageCustom(TLUpdateShortMessage update) {
		final IUser user = databaseManager.getUserById(update.getUserId());
		if (user != null) {
			BotLogger.info(LOGTAG, "Received message from: " + update.getUserId());
			messageHandler.handleMessage(user, update);
		}
	}

	/**
	 * 群组消息
	 * 
	 * @see org.telegram.bot.handlers.DefaultUpdatesHandler#onTLUpdateShortChatMessageCustom(org.telegram.api.updates.TLUpdateShortChatMessage)
	 */
	@Override
	protected void onTLUpdateShortChatMessageCustom(TLUpdateShortChatMessage update) {
		logger.info(messageHandler.getBotConfig().getPhoneNumber()+" chatMessage:chatid={},from={},msg={},date={}", update.getChatId(), update.getFromId(),
				update.getMessage(), update.getDate());
	}

	/**
	 * channel 消息.
	 * 
	 * @see org.telegram.bot.handlers.DefaultUpdatesHandler#onTLUpdateChannelNewMessageCustom(org.telegram.api.update.TLUpdateChannelNewMessage)
	 */
	@Override
	protected void onTLUpdateChannelNewMessageCustom(TLUpdateChannelNewMessage update) {
		logger.info(messageHandler.getBotConfig().getPhoneNumber()+" ChannelNewMessage:channelid={},ptsCount={},msg={} ", update.getChannelId(), update.getPtsCount(),
				update.getMessage());
		if (update.getMessage() instanceof TLMessage) {
			TLMessage m = (TLMessage) update.getMessage();
			logger.info(messageHandler.getBotConfig().getPhoneNumber()+" new message :from={},msg={},date={}", m.getFromId(), m.getMessage(), m.getDate());
		}
	}

	/**
	 * 用户状态变更.
	 * 
	 * @see org.telegram.bot.handlers.DefaultUpdatesHandler#onTLUpdateUserStatusCustom(org.telegram.api.update.TLUpdateUserStatus)
	 */
	@Override
	protected void onTLUpdateUserStatusCustom(TLUpdateUserStatus update) {
		 logger.info(messageHandler.getBotConfig().getPhoneNumber()+"  onTLUpdateUserStatusCustom,userid={},status={}",
		 update.getUserId(), update.getStatus());
	}

	@Override
	public void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
		onTLAbsMessageCustom(update.getMessage());
	}

	@Override
	protected void onTLAbsMessageCustom(TLAbsMessage message) {
		if (message instanceof TLMessage) {
			BotLogger.debug(LOGTAG, "Received TLMessage");
			onTLMessage((TLMessage) message);
		} else {
			BotLogger.warn(LOGTAG, "Unsupported TLAbsMessage -> " + message.toString());
		}
	}

	@Override
	protected void onUsersCustom(List<TLAbsUser> users) {
		usersHandler.onUsers(users);
	}

	@Override
	protected void onChatsCustom(List<TLAbsChat> chats) {
		chatsHandler.onChats(chats);
	}

	/**
	 * Handles TLMessage
	 * 
	 * @param message
	 *            Message to handle
	 */
	private void onTLMessage(@NotNull TLMessage message) {
		if (message.hasFromId()) {
			BotLogger.debug(LOGTAG, "Received TLMessage from=" + message.getFromId());
			final IUser user = databaseManager.getUserById(message.getFromId());
			if (user != null) {
				this.tlMessageHandler.onTLMessage(message);
			} else {
				logger.info(messageHandler.getBotConfig().getPhoneNumber()+"new user message:chatid :{},from: {},msg:{} ", message.getChatId(), message.getFromId(),
						message.getMessage());
			}
		}
	}
}
