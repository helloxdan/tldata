/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.tl.entity.UserSession;

/**
 * 用户会话DAO接口
 * @author admin
 * @version 2018-06-02
 */
@MyBatisDao
public interface UserSessionDao extends CrudDao<UserSession> {
	
}