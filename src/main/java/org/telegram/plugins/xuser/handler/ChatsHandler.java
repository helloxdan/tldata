package org.telegram.plugins.xuser.handler;

import java.util.List;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.TLChatForbidden;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelForbidden;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.services.BotLogger;
import org.telegram.plugins.xuser.IBotDataService;
import org.telegram.plugins.xuser.entity.ChatImpl;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Handler for received chats
 * @date 16 of October of 2016
 */
public class ChatsHandler implements IChatsHandler {
	private static final String LOGTAG = "CHATSHANDLER";
	private final IBotDataService databaseManager;

	public ChatsHandler(IBotDataService databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public void onChats(List<TLAbsChat> chats) {
		chats.forEach(this::onAbsChat);
	}

	private void onAbsChat(TLAbsChat chat) {
		if (chat instanceof TLChannel) {
			onChannel((TLChannel) chat);
		} else if (chat instanceof TLChannelForbidden) {
			onChannelForbidden((TLChannelForbidden) chat);
		} else if (chat instanceof TLChat) {
			onChat((TLChat) chat);
		} else if (chat instanceof TLChatForbidden) {
			onChatForbidden((TLChatForbidden) chat);
		} else {
			BotLogger.warn(LOGTAG, "Unsupported chat type " + chat);
		}
	}

	private void onChatForbidden(TLChatForbidden chat) {
		onChat(chat.getId(), chat.getTitle()+"(forbidden)");
	}

	private void onChat(TLChat chat) {

		onChat(chat.getId(), chat.getTitle());
	}

	private void onChat(int chatId, String title) {
		boolean updating = true;
		ChatImpl current = (ChatImpl) databaseManager.getChatById(chatId);
		if (current == null) {
			updating = false;
			// current = new ChatImpl(chatId);
			current = new ChatImpl(chatId, title);
		} else {
			current.setId(chatId);
			current.setTitle(title); 
		}
		current.setChannel(false);

		if (updating) {
			databaseManager.updateChat(current);
		} else {
			databaseManager.addChat(current);
		}
	}

	private void onChannelForbidden(TLChannelForbidden channel) {
		boolean updating = true;
		ChatImpl current = (ChatImpl) databaseManager.getChatById(channel.getId());
		if (current == null) {
			updating = false;
			// current = new ChatImpl(channel.getId());
			current = new ChatImpl(channel.getId(), channel.getTitle());
		}else {
			current.setId(channel.getId());
			current.setChatid(channel.getId());
			current.setTitle(channel.getTitle());
		}
		current.setChannel(true);
		current.setAccessHash(channel.getAccessHash());

		if (updating) {
			databaseManager.updateChat(current);
		} else {
			databaseManager.addChat(current);
		}
	}

	private void onChannel(TLChannel channel) {
		boolean updating = true;
		ChatImpl current = (ChatImpl) databaseManager.getChatById(channel.getId());
		if (current == null) {
			updating = false;
			// current = new ChatImpl(channel.getId());
			current = new ChatImpl(channel);
		} else {
			current.setId(channel.getId());
			current.setChatid(channel.getId());
			current.setTitle(channel.getTitle());
			current.setUsername(channel.getUsername());
		}
		current.setChannel(true);
		if (channel.hasAccessHash()) {
			current.setAccessHash(channel.getAccessHash());
		}

		if (updating) {
			databaseManager.updateChat(current);
		} else {
			databaseManager.addChat(current);
		}
	}

}
