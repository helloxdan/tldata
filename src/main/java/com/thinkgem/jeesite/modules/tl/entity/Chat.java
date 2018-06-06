/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 群组会话记录Entity
 * @author admin
 * @version 2018-06-02
 */
public class Chat extends DataEntity<Chat> {
	
	private static final long serialVersionUID = 1L;
	private String account;		// 所属账号
	private String chatid;		// 会话ID
	private Integer isChannel;		// 是否频道
	private String title;		// 群组名称
	private Long accesshash;		// 访问码
	private String ids;  //删除记录ids
	public Chat() {
		super();
	}

	public Chat(String id){
		super(id);
	}

	@Length(min=1, max=15, message="所属账号长度必须介于 1 和 15 之间")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	@NotNull(message="会话ID不能为空")
	public String getChatid() {
		return chatid;
	}

	public void setChatid(String chatid) {
		this.chatid = chatid;
	}
	
	@NotNull(message="是否频道不能为空")
	public Integer getIsChannel() {
		return isChannel;
	}

	public void setIsChannel(Integer isChannel) {
		this.isChannel = isChannel;
	}
	
	@Length(min=0, max=100, message="群组名称长度必须介于 0 和 100 之间")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Long getAccesshash() {
		return accesshash;
	}

	public void setAccesshash(Long accesshash) {
		this.accesshash = accesshash;
	}
	

		public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}