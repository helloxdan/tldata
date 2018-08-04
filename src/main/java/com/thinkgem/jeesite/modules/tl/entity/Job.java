/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 工作任务Entity
 * 
 * @author admin
 * @version 2018-06-02
 */
public class Job extends DataEntity<Job> {

	private static final long serialVersionUID = 1L;
	private String name; // 任务名称
	private String groupUrl; // 群组邀请码
	private String groupName; // 群组名称
	private Integer groupId; // 群组ID
	private Integer fromGroupId; // 来源群ID
	private String fromGroupUrl; // 来源群邀请码
	private String fromGroupName; // 来源群名称
	private int usernum; // 用户数
	private int accountNum; // 所需账号数量
	private Integer day; // 几天完成
	private String boss; // 老板
	private String status; // 任务状态
	private String ids; // 删除记录ids

	public Job() {
		super();
	}

	public Job(String id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupUrl() {
		return groupUrl;
	}

	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getFromGroupId() {
		return fromGroupId;
	}

	public void setFromGroupId(Integer fromGroupId) {
		this.fromGroupId = fromGroupId;
	}

	public String getFromGroupUrl() {
		return fromGroupUrl;
	}

	public void setFromGroupUrl(String fromGroupUrl) {
		this.fromGroupUrl = fromGroupUrl;
	}

	public String getFromGroupName() {
		return fromGroupName;
	}

	public void setFromGroupName(String fromGroupName) {
		this.fromGroupName = fromGroupName;
	}

	public int getUsernum() {
		return usernum;
	}

	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getBoss() {
		return boss;
	}

	public void setBoss(String boss) {
		this.boss = boss;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public int getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(int accountNum) {
		this.accountNum = accountNum;
	}
}