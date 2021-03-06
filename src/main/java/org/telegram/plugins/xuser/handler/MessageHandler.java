package org.telegram.plugins.xuser.handler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.RpcException;
import org.telegram.api.message.TLMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.XUtils;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class MessageHandler {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static final String LOGTAG = "MESSAGEHANDLER";
	private IKernelComm kernelComm;
	private BotConfig botConfig = null;
	public MessageHandler() {
	}

	public void setKernelComm(IKernelComm kernelComm) {
		this.kernelComm = kernelComm;
	}

	public BotConfig getBotConfig() {
		return botConfig;
	}

	public void setBotConfig(BotConfig botConfig) {
		this.botConfig = botConfig;
	}

	/**
	 * Handler for the request of a contact
	 *
	 * @param user
	 *            User to be answered
	 * @param message
	 *            TLMessage received
	 */
	public void handleMessage(@NotNull IUser user, @NotNull TLMessage message) {
		try { 
			handleMessageInternal(user, message.getMessage());
		} catch (RpcException e) {
			BotLogger.severe(LOGTAG, e);
		}
	}

	/**
	 * Handler for the requests of a contact
	 *
	 * @param user
	 *            User to be answered
	 * @param message
	 *            Message received
	 */
	public void handleMessage(@NotNull IUser user,
			@NotNull TLUpdateShortMessage message) {
		try { 
			handleMessageInternal(user, message.getMessage());
		} catch (RpcException e) {
			BotLogger.severe(LOGTAG, e);
		}
	}

	/**
	 * Handle a message from an user
	 * 
	 * @param user
	 *            User that sent the message
	 * @param message
	 *            Message received
	 */
	private void handleMessageInternal(@NotNull IUser user, String message)
			throws RpcException {
		// kernelComm.sendMessage(user, message);
		// kernelComm.performMarkAsRead(user, 0);

		// 记录谁发送消息
		logger.info(getBotConfig()==null ? "":getBotConfig().getPhoneNumber()+"-新消息,from={},message={}", user.getUserId(), XUtils.transChartset(message));
	}
}
