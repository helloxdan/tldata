/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 调度任务Entity
 * 
 * @author admin
 * @version 2018-06-02
 */
public class JobTask extends DataEntity<JobTask> {

	private static final long serialVersionUID = 1L;
	public static final String STATUS_NONE="0";
	public static final String STATUS_FETCHED="1";
	public static final String STATUS_JOIN="2";
	
	private String action; // 动作标识
	
	
	private String jobId; // 任务ID
	private String jobGroupUrl; //  
	private String account; // 登录账号
	private String type; // 任务类型
	private String groupId; // 来源群组链接
	private String groupUrl; // 来源群组链接
	private Integer offsetNum; // 开始位置
	private Integer limitNum; // 记录数
	private String status; // 是否已经完成
	private Integer usernum; // 获取用户数
	private Integer beginOffset; // 开始 开始位置
	private Integer endOffset; // 结束 开始位置
	private String ids; // 删除记录ids

	private Job job;

	public JobTask() {
		super();
	}

	public JobTask(String id) {
		super(id);
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	 

	public Integer getOffsetNum() {
		return offsetNum;
	}

	public void setOffsetNum(Integer offsetNum) {
		this.offsetNum = offsetNum;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getUsernum() {
		return usernum;
	}

	public void setUsernum(Integer usernum) {
		this.usernum = usernum;
	}

	public Integer getBeginOffset() {
		return beginOffset;
	}

	public void setBeginOffset(Integer beginOffset) {
		this.beginOffset = beginOffset;
	}

	public Integer getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(Integer endOffset) {
		this.endOffset = endOffset;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getGroupUrl() {
		return groupUrl;
	}

	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}

	public String getJobGroupUrl() {
		return jobGroupUrl;
	}

	public void setJobGroupUrl(String jobGroupUrl) {
		this.jobGroupUrl = jobGroupUrl;
	}
	
	
}