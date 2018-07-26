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
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;
import com.thinkgem.jeesite.modules.tl.dao.JobGroupDao;

/**
 * 任务采集群组列表Service
 * @author admin
 * @version 2018-07-25
 */
@Service
@Transactional(readOnly = true)
public class JobGroupService extends CrudService<JobGroupDao, JobGroup> {

	public JobGroup get(String id) {
		return super.get(id);
	}
	
	public List<JobGroup> findList(JobGroup jobGroup) {
		return super.findList(jobGroup);
	}
	
	public Page<JobGroup> findPage(Page<JobGroup> page, JobGroup jobGroup) {
		return super.findPage(page, jobGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(JobGroup jobGroup) {
		super.save(jobGroup);
	}
	
	@Transactional(readOnly = false)
	public void delete(JobGroup jobGroup) {
		super.delete(jobGroup);
	}
	
	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				JobGroup jobGroup = new JobGroup(id);
				super.delete(jobGroup);
			}
		}
		// 删除缓存
		//IcareUtils.removeCache();
	}

	public List<JobGroup> findValidList(JobGroup jobGroup) {
		return this.dao.findValidList(jobGroup);
	}
}