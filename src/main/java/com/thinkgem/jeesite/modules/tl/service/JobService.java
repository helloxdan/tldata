/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.work.TaskData;
import org.telegram.plugins.xuser.work.TaskQuery;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.sys.listener.WebContextListener;
import com.thinkgem.jeesite.modules.tl.dao.JobDao;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.support.DaemonThreadFactory;
import com.thinkgem.jeesite.modules.utils.Constants;

/**
 * 工作任务Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class JobService extends CrudService<JobDao, Job> implements TaskQuery {

	@Autowired
	private JobGroupService jobGroupService;

	@Autowired
	private JobTaskService jobTaskService;
	private static int[] lock = new int[] { 1 };

	public JobService() {
		WebContextListener.addExecutorService(jobTaskThreadPool);
		WebContextListener.addExecutorService(scheduledThreadPool);
	}

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
		// super.save(job);
		if (job.getAccountNum() == -1) {
			// 根据所需要拉的人数，推算出需要的账号
			Integer accountNum = (int) (job.getUsernum() / Constants.USER_LIMIT_SIZE);
			job.setAccountNum(accountNum);
		}
		if (job.getIsNewRecord()) {
			job.preInsert();
			dao.insert(job);
		} else {
			job.preUpdate();
			dao.update(job);
		}
	}

	@Transactional(readOnly = false)
	public void delete(Job job) {
		super.delete(job);

		// delete job data
		// TODO
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
		// IcareUtils.removeCache();
	}

	public JSONObject getRpcCallInfoByTaskid(String taskid) {
		JobTask jobTask = new JobTask(taskid);
		return this.dao.getRpcCallInfoByTaskid(jobTask);
	}

	// public TaskData getTaskData2(String jobid) {
	// JobTask jobtask = new JobTask();
	// jobtask.setJobId(jobid);
	// jobtask.setStatus(JobTask.STATUS_NONE);//
	// // JobTask task = findOneOfJob(jobtask);
	// TaskData data = new TaskData();
	// data.setTaskid(task.getId());
	// data.setDestGroupUrl(task.getJobGroupUrl());
	// data.setSrcGroupUrl(task.getGroupUrl());
	// data.setOffset(task.getOffsetNum() == null ? 0 : task.getOffsetNum());
	// data.setLimit(task.getLimitNum() == null ? Constants.FETCH_TASK_USER_NUM
	// : task.getLimitNum());
	//
	// logger.info("任务数据，{}", JsonMapper.toJsonString(data));
	// return data;
	//
	// }

	@Override
	public TaskData getTaskData(String jobid, String phone) {
		if (jobGroupList.size() == 0) {

			return null;
		}

		TaskData data = new TaskData();
		// 多线程，保持获取数据同步
		synchronized (lock) {
			// 排序
			Collections.sort(jobGroupList);

			JobGroup jobGroup = jobGroupList.get(0);
			// data.setTaskid(null);
			data.setDestGroupUrl(job.getGroupUrl());
			data.setSrcGroupUrl(jobGroup.getGroupUrl());
			data.setSrcGroupName(jobGroup.getGroupName());
			data.setOffset(jobGroup.getOffset());// 看是查询位置
			data.setLimit(Constants.FETCH_PAGE_SIZE);
			// ！！！！！位置前移，就是下次读取的位置
			jobGroup.setOffset(jobGroup.getOffset() + Constants.FETCH_PAGE_SIZE);

			// 检查jobGroup的offset到达10000，就删除该jobGroup
			if (jobGroup.getOffset() >= 9900) {
				jobGroupList.remove(0);
			}

			// ！！！！
			// logger.info("{},{},offset={}",phone,jobGroup.getGroupUrl(),jobGroup.getOffset());

			// 将数据存储到数据
			JobTask jobtask = new JobTask();
			jobtask.setJobId(jobid);
			jobtask.setStatus(JobTask.STATUS_NONE);//
			jobtask.setJobGroupUrl(job.getGroupUrl());
			jobtask.setAccount(phone);
			jobtask.setGroupId(jobGroup.getGroupId());
			jobtask.setGroupUrl(jobGroup.getGroupUrl());
			jobtask.setOffsetNum(jobGroup.getOffset());
			jobtask.setLimitNum(Constants.FETCH_PAGE_SIZE);

			// 放入队列中，存储数据库
			jobTaskThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					saveJobTask(jobtask);
				}

			});
		}

		logger.debug("任务数据，{}", JsonMapper.toJsonString(data));
		return data;

	}

	@Transactional(readOnly = false)
	public void saveJobTask(JobTask jobtask) {
		jobTaskService.save(jobtask);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateJobGroup() {
		if (jobGroupList.size() > 0)
			logger.info("开始更新jobGroup数据");
		// 主要更新offset的值，便于重启的时候，能从正确的位置开始
		for (JobGroup jobGroup : jobGroupList) {
			jobGroupService.updateOffset(jobGroup);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteTaskGroup(XUserBot bot, TaskData data) {
		// JobTask jobTask = new JobTask(data.getTaskid());
		// delete(jobTask);
		synchronized (jobGroupList) {
			String groupLink = data.getSrcGroupUrl();
			Iterator<JobGroup> iter = jobGroupList.iterator();
			for (Iterator iterator = jobGroupList.iterator(); iterator
					.hasNext();) {
				JobGroup jobGroup = (JobGroup) iterator.next();
				if (jobGroup.getGroupUrl().equals(groupLink)) {
					iterator.remove();
				}
			}

		}
	}

	// 保存jobtask数据线程
	ExecutorService jobTaskThreadPool = Executors.newFixedThreadPool(1,
			new DaemonThreadFactory());
	// 用于定期更新JobGroupList中的数据到数据库中
	ScheduledExecutorService scheduledThreadPool = Executors
			.newScheduledThreadPool(1, new DaemonThreadFactory());
	// 存放所有采集的群组
	List<JobGroup> jobGroupList = new ArrayList<JobGroup>();
	Job job = null;

	/**
	 * 将采集的群组列表放入内容，供work获取任务数据。
	 * 
	 * @param job
	 */
	public int initRunData(Job job) {
		this.job = job;
		JobGroup jobGroup = new JobGroup();
		jobGroup.setJobId(job.getId());
		jobGroupList = jobGroupService.findValidList(jobGroup);
		int jobGroupNum = jobGroupList.size();

		long period = Long
				.parseLong(Global.getConfig("job.updategroup.period"));
		// 两分钟执行一次
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				updateJobGroup();
			}
		}, 1, period, TimeUnit.SECONDS);
		// FIXME 修改更新jobGroup数据的频率

		return jobGroupNum;
	}
}