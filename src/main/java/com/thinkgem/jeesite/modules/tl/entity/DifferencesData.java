/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 差异数据Entity
 * @author admin
 * @version 2018-06-02
 */
public class DifferencesData extends DataEntity<DifferencesData> {
	
	private static final long serialVersionUID = 1L;
	private String account;		// 登录账号
	private Integer botid;		// botid
	private Integer pts;		// pts
	private Integer date;		// date
	private Integer seq;		// seq
	private String ids;  //删除记录ids
	public DifferencesData() {
		super();
	}

	public DifferencesData(String id){
		super(id);
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	public Integer getBotid() {
		return botid;
	}

	public void setBotid(Integer botid) {
		this.botid = botid;
	}
	
	public Integer getPts() {
		return pts;
	}

	public void setPts(Integer pts) {
		this.pts = pts;
	}
	
	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	

		public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}