/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 用户会话Entity
 * 
 * @author admin
 * @version 2018-06-02
 */
public class UserSession extends DataEntity<UserSession> {

	private static final long serialVersionUID = 1L;
	private String account; // 登录账号
	private Integer userid; // 用户id
	private Long userhash; // 访问码
	private String username; // 用户名称
	private String fromGroup;
	private String fromGroupName;
	private String ids; // 删除记录ids

	public UserSession() {
		super();
	}

	public UserSession(String id) {
		super(id);
	}

	@Length(min = 1, max = 15, message = "登录账号长度必须介于 1 和 15 之间")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@NotNull(message = "用户id不能为空")
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Long getUserhash() {
		return userhash;
	}

	public void setUserhash(Long userhash) {
		this.userhash = userhash;
	}

	@Length(min = 1, max = 100, message = "用户名称长度必须介于 1 和 100 之间")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}

	public String getFromGroupName() {
		return fromGroupName;
	}

	public void setFromGroupName(String fromGroupName) {
		this.fromGroupName = fromGroupName;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}