/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;

/**
 * 邀请用户DAO接口
 * @author admin
 * @version 2018-06-02
 */
@MyBatisDao
public interface JobUserDao extends CrudDao<JobUser> {

	void deleteRepeatJobUser(JobUser jobUser);

	List<JobUser> findUserOfJob(JobUser ju);

	List<JobUser> findDistinctForJob(JobUser jobUser);

	void updateStatus(JobUser jobUser);
	
}