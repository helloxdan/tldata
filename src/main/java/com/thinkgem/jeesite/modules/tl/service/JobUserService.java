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
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.dao.JobUserDao;

/**
 * 邀请用户Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class JobUserService extends CrudService<JobUserDao, JobUser> {

	public JobUser get(String id) {
		return super.get(id);
	}

	public List<JobUser> findList(JobUser jobUser) {
		return super.findList(jobUser);
	}

	public Page<JobUser> findPage(Page<JobUser> page, JobUser jobUser) {
		return super.findPage(page, jobUser);
	}

	@Transactional(readOnly = false)
	public void save(JobUser jobUser) {
		super.save(jobUser);
	}

	@Transactional(readOnly = false)
	public void delete(JobUser jobUser) {
		super.delete(jobUser);
	}

	@Transactional(readOnly = false)
	public void del(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] delIds = ids.split(",");
			for (String id : delIds) {
				JobUser jobUser = new JobUser(id);
				super.delete(jobUser);
			}
		}
		// 删除缓存
		// IcareUtils.removeCache();
	}

	/**
	 * 清除无效的用户数据。
	 * 
	 * @param jobid
	 */
	@Transactional(readOnly = false)
	public void cleanJobUser(String jobid) {
		// TODO 清洗用户数据
		// 重复的用户id
		JobUser jobUser = new JobUser();
		jobUser.setJobId(jobid);
		this.dao.deleteRepeatJobUser(jobUser);
	}

	/**
	 * 将用户写入任务用户表中，如果用户已经在表中了，则忽略
	 * 
	 * @param ju
	 * @param string
	 */
	@Transactional(readOnly = false)
	public void insertUserToJob(JobUser ju, String jobId) {
		// 先判断job=auto下是否存在此用户
		ju.setJobId(jobId);
		List<JobUser> list = this.dao.findUserOfJob(ju);
		if (list.size() > 0) {
			// 用户已经存在
			logger.info("{}用户已存在job={}", ju.getUsername(), jobId);
		} else {
			save(ju);
		}
	}

	public List<JobUser> findDistinctForJob(JobUser jobUser) {
		return this.dao.findDistinctForJob(jobUser); 
	}

	public void updateStatus(JobUser jobUser) {
		jobUser.preUpdate();
		this.dao.updateStatus(jobUser); 
	}
}