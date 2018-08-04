/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 任务采集群组列表Entity
 * 
 * @author admin
 * @version 2018-07-25
 */
public class JobGroup extends DataEntity<JobGroup> implements
		Comparable<JobGroup> {

	private static final long serialVersionUID = 1L;
	private String jobId; // 任务ID
	private String groupId; // 群组ID
	private String groupName; // 群组名称
	private String groupUrl; // 群组link
	private int usernum; // 用户数量
	private int offset; // 抽取数据的索引偏移数
	private Date upcateDate; // upcate_date
	private String ids; // 删除记录ids

	public JobGroup() {
		super();
	}

	public JobGroup(String id) {
		super(id);
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupUrl() {
		return groupUrl;
	}

	public void setGroupUrl(String groupUrl) {
		this.groupUrl = groupUrl;
	}

	public int getUsernum() {
		return usernum;
	}

	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpcateDate() {
		return upcateDate;
	}

	public void setUpcateDate(Date upcateDate) {
		this.upcateDate = upcateDate;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	@Override
	public int compareTo(JobGroup o) {
		return this.offset <= o.getOffset() ? -1 : 1;
	}
}