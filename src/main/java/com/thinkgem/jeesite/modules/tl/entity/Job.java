/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 工作任务Entity
 * @author admin
 * @version 2018-06-02
 */
public class Job extends DataEntity<Job> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 任务名称
	private String groupUrl;		// 群组邀请码
	private String groupName;		// 群组名称
	private Integer groupId;		// 群组ID
	private Integer fromGroupId;		// 来源群ID
	private String fromGroupUrl;		// 来源群邀请码
	private String fromGroupName;		// 来源群名称
	private Integer usernum;		// 用户数
	private Integer day;		// 几天完成
	private String boss;		// 老板
	private String status;		// 任务状态
	private String ids;  //删除记录ids
	public Job() {
		super();
	}

	public Job(String id){
		super(id);
	}

	@Length(min=0, max=100, message="任务名称长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=200, message="群组邀请码长度必须介于 0 和 200 之间")
	public String getGroupUrl() {
		return groupUrl;
	}

	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}
	
	@Length(min=0, max=200, message="群组名称长度必须介于 0 和 200 之间")
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
	
	@Length(min=0, max=200, message="来源群邀请码长度必须介于 0 和 200 之间")
	public String getFromGroupUrl() {
		return fromGroupUrl;
	}

	public void setFromGroupUrl(String fromGroupUrl) {
		this.fromGroupUrl = fromGroupUrl;
	}
	
	@Length(min=0, max=50, message="来源群名称长度必须介于 0 和 50 之间")
	public String getFromGroupName() {
		return fromGroupName;
	}

	public void setFromGroupName(String fromGroupName) {
		this.fromGroupName = fromGroupName;
	}
	
	public Integer getUsernum() {
		return usernum;
	}

	public void setUsernum(Integer usernum) {
		this.usernum = usernum;
	}
	
	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}
	
	@Length(min=1, max=100, message="老板长度必须介于 1 和 100 之间")
	public String getBoss() {
		return boss;
	}

	public void setBoss(String boss) {
		this.boss = boss;
	}
	
	@Length(min=1, max=30, message="任务状态长度必须介于 1 和 30 之间")
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
}