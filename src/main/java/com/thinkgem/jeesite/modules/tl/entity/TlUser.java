/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 好友用户Entity
 * @author admin
 * @version 2018-06-02
 */
public class TlUser extends DataEntity<TlUser> {
	
	private static final long serialVersionUID = 1L;
	private String username;		// 用户名
	private String langcode;		// 语言区
	private String firstname;		//  
	private String lastname;		//  
	private Date msgTime;		// 最新发言时间
	private Date beginMsgTime;		// 开始 最新发言时间
	private Date endMsgTime;		// 结束 最新发言时间
	private String ids;  //删除记录ids
	public TlUser() {
		super();
	}

	public TlUser(String id){
		super(id);
	}

	@Length(min=0, max=50, message="用户名长度必须介于 0 和 50 之间")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getMsgTime() {
		return msgTime;
	}

	public String getLangcode() {
		return langcode;
	}

	public void setLangcode(String langcode) {
		this.langcode = langcode;
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

	public void setMsgTime(Date msgTime) {
		this.msgTime = msgTime;
	}
	
	public Date getBeginMsgTime() {
		return beginMsgTime;
	}

	public void setBeginMsgTime(Date beginMsgTime) {
		this.beginMsgTime = beginMsgTime;
	}
	
	public Date getEndMsgTime() {
		return endMsgTime;
	}

	public void setEndMsgTime(Date endMsgTime) {
		this.endMsgTime = endMsgTime;
	}
		

		public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}