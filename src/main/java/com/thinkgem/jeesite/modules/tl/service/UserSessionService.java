/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.entity.ChatImpl;
import org.telegram.plugins.xuser.entity.User;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.entity.Chat;
import com.thinkgem.jeesite.modules.tl.entity.TlUser;
import com.thinkgem.jeesite.modules.tl.entity.UserSession;
import com.thinkgem.jeesite.modules.tl.dao.UserSessionDao;

/**
 * 用户会话Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class UserSessionService extends
		CrudService<UserSessionDao, UserSession> {

	public UserSession get(String id) {
		return super.get(id);
	}

	public List<UserSession> findList(UserSession userSession) {
		return super.findList(userSession);
	}

	public Page<UserSession> findPage(Page<UserSession> page,
			UserSession userSession) {
		return super.findPage(page, userSession);
	}

	@Transactional(readOnly = false)
	public void save(UserSession userSession) {
		super.save(userSession);
	}

	@Transactional(readOnly = false)
	public void delete(UserSession userSession) {
		super.delete(userSession);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				UserSession userSession = new UserSession(id);
				super.delete(userSession);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	public @Nullable IUser getUserById(String phone, int userId) {
		User user = null;
		// UserSession u = new UserSession();
		// u.setAccount(phone);
		// u.setUserid(userId);
		// List<UserSession> list = findList(u);
		// if (list.size() > 0) {
		UserSession u = get(phone + userId);
		if (u != null) {
			// u = list.get(0);
			user = new User(userId);
			user.setUserHash(u.getUserhash());
			user.setUsername(u.getUsername());
		} else {
			logger.debug("{}用户会话id={}不存在", phone, userId);
		}
		return user;
	}
}