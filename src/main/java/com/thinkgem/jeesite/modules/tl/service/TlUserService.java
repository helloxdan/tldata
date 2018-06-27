/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.entity.TlUser;
import com.thinkgem.jeesite.modules.tl.dao.TlUserDao;

/**
 * 好友用户Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class TlUserService extends CrudService<TlUserDao, TlUser> {

	public TlUser get(String id) {
		return super.get(id);
	}

	public List<TlUser> findList(TlUser tlUser) {
		return super.findList(tlUser);
	}

	public Page<TlUser> findPage(Page<TlUser> page, TlUser tlUser) {
		return super.findPage(page, tlUser);
	}

	@Transactional(readOnly = false)
	public void save(TlUser tlUser) {
		super.save(tlUser);
	}

	@Transactional(readOnly = false)
	public void delete(TlUser tlUser) {
		super.delete(tlUser);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				TlUser tlUser = new TlUser(id);
				super.delete(tlUser);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	@Transactional(readOnly = false)
	public void updateMsgNum(TlUser tlUser) {
		tlUser.preUpdate();
		this.dao.updateMsgNum(tlUser);
	}

	@Transactional(readOnly = false)
	public void insertOrUpdate(TlUser tlUser) {
		TlUser u = get(tlUser.getId());
		if (u == null) {
			tlUser.setIsNewRecord(true);
		} else {
			tlUser.setIsNewRecord(false);
			tlUser.preUpdate();
		}
		save(tlUser);
	}
}