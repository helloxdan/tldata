/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.dao.JobDao;

/**
 * 工作任务Service
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class JobService extends CrudService<JobDao, Job> {

	public Job get(String id) {
		return super.get(id);
	}
	
	public List<Job> findList(Job job) {
		return super.findList(job);
	}
	
	public Page<Job> findPage(Page<Job> page, Job job) {
		return super.findPage(page, job);
	}
	
	@Transactional(readOnly = false)
	public void save(Job job) {
		super.save(job);
	}
	
	@Transactional(readOnly = false)
	public void delete(Job job) {
		super.delete(job);
	}
	
	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				Job job = new Job(id);
				super.delete(job);
			}
		}
		// 删除缓存
		//IcareUtils.removeCache();
	}

	public JSONObject getRpcCallInfoByTaskid(String taskid) {
		JobTask jobTask=new JobTask(taskid);
		return this.dao.getRpcCallInfoByTaskid(jobTask);
	}
}