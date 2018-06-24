/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;

/**
 * 群组DAO接口
 * @author admin
 * @version 2018-06-02
 */
@MyBatisDao
public interface GroupDao extends CrudDao<Group> {
	
	void updateUpdateNum(Group group);
	void updateOffset(Group group);
	List<Group> findListWithoutUrl(Group group);
}