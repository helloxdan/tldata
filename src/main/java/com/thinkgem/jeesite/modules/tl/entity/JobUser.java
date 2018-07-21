/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.utils.Constants;

/**
 * 邀请用户Entity
 * 
 * @author admin
 * @version 2018-06-02
 */
public class JobUser extends DataEntity<JobUser> {

	private static final long serialVersionUID = 1L;
	private String jobId; // 工作ID
	private String taskId; // 分派任务ID
	private String account; // 登录账号
	private String fromGroup; // 用户来源群组
	private String fromGroupName; // 用户来源群组
	private String userid; // 用户ID
	private String username; // 用户名
	private String firstname;		//  
	private String lastname;		//  
	private Long userHash; // 访问码
	private String status; // 是否已邀请
	private String ids; // 删除记录ids
	private Date msgTime;		// 最新发言时间
	private int star=0;//用于查询用户
	
	// 用于查询，限定查询结果数量
	private int limit = Constants.USER_LIMIT_SIZE;
	//用户查询
	private String toJobid;

	public JobUser() {
		super();
	}

	public JobUser(String id) {
		super(id);
	}

	@Length(min = 0, max = 64, message = "任务ID长度必须介于 0 和 64 之间")
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Length(min = 0, max = 64, message = "登录账号长度必须介于 0 和 64 之间")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Length(min = 0, max = 100, message = "用户名长度必须介于 0 和 100 之间")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUserHash() {
		return userHash;
	}

	public void setUserHash(Long userHash) {
		this.userHash = userHash;
	}

	@Length(min = 0, max = 1, message = "是否已邀请长度必须介于 0 和 1 之间")
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getToJobid() {
		return toJobid;
	}

	public void setToJobid(String toJobid) {
		this.toJobid = toJobid;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Date getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(Date msgTime) {
		this.msgTime = msgTime;
	}

	public String getFromGroupName() {
		return fromGroupName;
	}

	public void setFromGroupName(String fromGroupName) {
		this.fromGroupName = fromGroupName;
	}
	
}