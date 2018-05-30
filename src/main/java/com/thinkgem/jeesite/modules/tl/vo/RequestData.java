package com.thinkgem.jeesite.modules.tl.vo;

import java.io.Serializable;

public class RequestData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String phone;
	private String chatId;
	private String chatAccessHash;
	private String userId;
	private String userName;
	private String userAccessHash;
	private boolean isChannel;
	private String code;
	private String url;
	private boolean isAdmin;
	private String ids;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getChatAccessHash() {
		return chatAccessHash;
	}

	public void setChatAccessHash(String chatAccessHash) {
		this.chatAccessHash = chatAccessHash;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAccessHash() {
		return userAccessHash;
	}

	public void setUserAccessHash(String userAccessHash) {
		this.userAccessHash = userAccessHash;
	}

	public boolean isChannel() {
		return isChannel;
	}

	public void setChannel(boolean isChannel) {
		this.isChannel = isChannel;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
