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
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;
import com.thinkgem.jeesite.modules.tl.dao.JobTaskDao;

/**
 * 调度任务Service
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class JobTaskService extends CrudService<JobTaskDao, JobTask> {

	public JobTask get(String id) {
		return super.get(id);
	}
	
	public List<JobTask> findList(JobTask jobTask) {
		return super.findList(jobTask);
	}
	
	public Page<JobTask> findPage(Page<JobTask> page, JobTask jobTask) {
		return super.findPage(page, jobTask);
	}
	
	@Transactional(readOnly = false)
	public void save(JobTask jobTask) {
		super.save(jobTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(JobTask jobTask) {
		super.delete(jobTask);
	}
	
	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				JobTask jobTask = new JobTask(id);
				super.delete(jobTask);
			}
		}
		// 删除缓存
		//IcareUtils.removeCache();
	}

	public String addTasks(RequestData data) {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject getRpcCallInfo(String taskid) {
		JobTask jobTask=new JobTask(taskid);
		return this.dao.getRpcCallInfo(jobTask);
	}
}