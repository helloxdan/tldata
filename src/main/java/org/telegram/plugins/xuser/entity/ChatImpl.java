package org.telegram.plugins.xuser.entity;

import org.telegram.api.chat.channel.TLChannel;
import org.telegram.bot.structure.Chat;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class ChatImpl implements Chat {
    private int id;
    private int chatid;
    private Long accessHash;
    private boolean isChannel;
    private String title;
    private String username;
    public ChatImpl(int id) {
        this.id = id;
        this.chatid=id;
    }

    public ChatImpl() {
    }

    public ChatImpl(TLChannel channel) {
		this.id=channel.getId();
		this.chatid=channel.getId();
		this.title= channel.getTitle();
		this.username=channel.getUsername();
	}

	public ChatImpl(int id2, String title2) {
		this.id=id2;
		this.chatid=id2;
		this.title=title2;
	}

	@Override
    public int getId() {
        return id;
    }

    @Override
    public Long getAccessHash() {
        return accessHash;
    }

    @Override
    public boolean isChannel() {
        return isChannel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccessHash(Long accessHash) {
        this.accessHash = accessHash;
    }

    public void setChannel(boolean channel) {
        isChannel = channel;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getChatid() {
		return chatid;
	}

	public void setChatid(int chatid) {
		this.chatid = chatid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
    
}
