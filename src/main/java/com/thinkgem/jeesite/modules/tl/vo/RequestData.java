package com.thinkgem.jeesite.modules.tl.vo;

import java.io.Serializable;

public class RequestData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String jobid;
	private String taskid;
	private String phone;
	private int chatId;
	private long chatAccessHash;
	private int userId;
	private String userName;
	private String userAccessHash;
	private boolean isChannel;
	private String code;
	private String url;
	private String hash;
	private boolean isAdmin;
	private int num;
	private int offset;
	private int limit;
	private String type;
	private String ids;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public long getChatAccessHash() {
		return chatAccessHash;
	}

	public void setChatAccessHash(long chatAccessHash) {
		this.chatAccessHash = chatAccessHash;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

}
