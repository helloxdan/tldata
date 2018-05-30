package org.telegram.plugins.echo.structure;

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
    private Long accessHash;
    private boolean isChannel;
    private String title;

    public ChatImpl(int id) {
        this.id = id;
    }

    public ChatImpl() {
    }

    public ChatImpl(TLChannel channel) {
		this.id=channel.getId();
		this.title=channel.getTitle();
	}

	public ChatImpl(int id2, String title2) {
		this.id=id2;
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
    
}
