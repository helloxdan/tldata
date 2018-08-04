/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 群组Entity
 * 
 * @author admin
 * @version 2018-06-02
 */
public class Group extends DataEntity<Group> {

	private static final long serialVersionUID = 1L;
	private String name; // 群组名称
	private String username; // 群组
	private String url; // 邀请链接
	private String isChannel; // 是否频道
	private Integer usernum; // 用户数
	private Integer updateNum; // 更新次数
	private Integer offset; // 抽取数据索引偏移数
	private Date upcateDate; // upcate_date
	private String status;
	private String out;// 排除在外，不做入群、采集用户等操作
	private String ids; // 删除记录ids

	private Long accesshash; // 访问码
	private String account; // 所属账号

	public Group() {
		super();
	}

	public Group(String id) {
		super(id);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsChannel() {
		return isChannel;
	}

	public void setIsChannel(String isChannel) {
		this.isChannel = isChannel;
	}

	public Integer getUsernum() {
		return usernum == null ? 0 : usernum;
	}

	public void setUsernum(Integer usernum) {
		this.usernum = usernum;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpcateDate() {
		return upcateDate;
	}

	public void setUpcateDate(Date upcateDate) {
		this.upcateDate = upcateDate;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
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

	public Integer getUpdateNum() {
		return updateNum;
	}

	public void setUpdateNum(Integer updateNum) {
		this.updateNum = updateNum;
	}

	public Integer getOffset() {
		return offset == null ? 0 : offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Long getAccesshash() {
		return accesshash == null ? 0L : accesshash;
	}

	public void setAccesshash(Long accesshash) {
		this.accesshash = accesshash;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}