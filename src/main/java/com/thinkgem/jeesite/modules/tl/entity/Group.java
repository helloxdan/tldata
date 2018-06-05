/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import org.hibernate.validator.constraints.Length;
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
	private String url; // 邀请码
	private String isChannel; // 是否频道
	private Integer usernum; // 用户数
	private Date upcateDate; // upcate_date
	private String status;
	private String ids; // 删除记录ids

	public Group() {
		super();
	}

	public Group(String id) {
		super(id);
	}

	@Length(min = 0, max = 200, message = "群组名称长度必须介于 0 和 200 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min = 0, max = 100, message = "邀请码长度必须介于 0 和 100 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Length(min = 0, max = 1, message = "是否频道长度必须介于 0 和 1 之间")
	public String getIsChannel() {
		return isChannel;
	}

	public void setIsChannel(String isChannel) {
		this.isChannel = isChannel;
	}

	public Integer getUsernum() {
		return usernum;
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