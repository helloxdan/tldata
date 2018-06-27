/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 登录账号Entity
 * 
 * @author admin
 * @version 2018-06-02
 */
public class Account extends DataEntity<Account> {

	private static final long serialVersionUID = 1L;
	public static final String STATUS_NONE = "none";
	public static final String STATUS_READY = "ready";
	public static final String STATUS_RUN = "run";

	private String name; // 用户名
	private Date loginDate; // 登录时间
	private String status; // 用户状态
	private String ids; // 删除记录ids
	private int usernum;
	private int groupnum;
	
	//最少用户数，默认100个
	private int minUsernum=100;
	//用于查询，限定用户数量
	private int limit=10;
	private String jobid;

	public Account() {
		super();
	}

	public Account(String id) {
		super(id);
	}

	@Length(min = 0, max = 15, message = "用户名长度必须介于 0 和 15 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Length(min = 0, max = 50, message = "用户状态长度必须介于 0 和 50 之间")
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

	public int getUsernum() {
		return usernum;
	}

	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public int getGroupnum() {
		return groupnum;
	}

	public void setGroupnum(int groupnum) {
		this.groupnum = groupnum;
	}

	public int getMinUsernum() {
		return minUsernum;
	}

	public void setMinUsernum(int minUsernum) {
		this.minUsernum = minUsernum;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	
}