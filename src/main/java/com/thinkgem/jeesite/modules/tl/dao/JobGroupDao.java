/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;

/**
 * 任务采集群组列表DAO接口
 * @author admin
 * @version 2018-07-25
 */
@MyBatisDao
public interface JobGroupDao extends CrudDao<JobGroup> {

	List<JobGroup> findValidList(JobGroup jobGroup);
	
}