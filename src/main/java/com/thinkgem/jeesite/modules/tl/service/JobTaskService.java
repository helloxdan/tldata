/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.tl.dao.JobTaskDao;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * 调度任务Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@Service
@Transactional(readOnly = true)
public class JobTaskService extends CrudService<JobTaskDao, JobTask> {
	@Autowired
	private AccountService accountService;
	@Autowired
	private JobUserService jobUserService;
	@Autowired
	private BotService botService;
	@Autowired
	private GroupService groupService;

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
		if (jobTask.getIsNewRecord())
			jobTask.setStatus("0");
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
		// IcareUtils.removeCache();
	}

	public static void main(String[] args) {
		/* System.out.println(50/40D+"-"+Math.ceil(50/40D)); */
		int num = (int) Math.ceil(50 / 40D);
		System.out.println(num);
	}

	/**
	 * 批量增加任务数。
	 * 
	 * @param data
	 * @return
	 */
	@Transactional(readOnly = false)
	public String addTasks(RequestData data) {
		String msg = "";
		String type = data.getType();
		String jobid = data.getJobid();
		// 计划拉取总用户数
		int toUserNum = data.getNum();

		// 需要账号数量，总人数/40
		int num = (int) Math.ceil(toUserNum / 40D);

		if ("any".equals(type)) {
			// 用户来自不同群组，任意选择
			// TODO 优先从现有的用户中直接复制

			// 分配任务到指定数量的账号上
			dispatchUserToAccount(jobid, num, data);

		} else {
			// 需要指定群组
			if (StringUtils.isBlank(data.getUrl())) {
				throw new RuntimeException("来源群组url不能为空");
			}
			// 根据邀请link获取群组id
			int chatId = botService.getGroupidByUrl(data.getUrl());

			// 从账户表中查询‘运行中’状态的记录，最多num条记录
			Account account = new Account();
			// account.setStatus(Account.STATUS_RUN);// 运行中状态
			account.setLimit(num);
			account.setJobid(jobid);
			List<Account> alist = accountService.findAccountForJob(account);
			if (alist.size() < num) {
				num = alist.size();
				msg = msg + " 只有" + num + "个账号可用,全部提交运行。您需要更多的账号,才能满足需求！！";
			}

			// 只取固定数量的账号
			// List<Account> list = new ArrayList<Account>();
			// for (int i = 0; i < num; i++) {
			// list.add(alist.get(i));
			// }

			// int offsetNum = data.getOffset();
			// int limitNum = data.getLimit();

			// 按规则批量生成任务。
			for (Account ac : alist) {
				// 总数超过10000，取消。每个群组最多拉取10000人
				// if (offsetNum + limitNum > 10000) {
				// msg = msg + " 拉取人数超过10000人，退出；";
				// break;
				// }

				JobTask jobTask = new JobTask();
				jobTask.setIsNewRecord(true);
				jobTask.preInsert();

				jobTask.setType("fetch");
				jobTask.setJobId(jobid);
				jobTask.setAccount(ac.getId());
				jobTask.setGroupId(chatId + "");
				jobTask.setGroupUrl(data.getUrl());
				// jobTask.setOffsetNum(offsetNum);
				jobTask.setLimitNum(50);// 默认每次查询50个用户，比 40多一些
				jobTask.setUsernum(0);
				jobTask.setStatus(JobTask.STATUS_NONE);// 未抽取

				// offsetNum = offsetNum + limitNum;

				save(jobTask);// 保存记录
			}

			// 循环拉取用户，直到满足每个账号有40个用户
			fetchUserToAccountFromGroup(jobid);

		}
		return msg;
	}

	/**
	 * 根据分配的账号，从知道群组中拉取用户，知道满足40个用户。
	 * 
	 * @param jobid
	 */
	@Transactional(readOnly = false)
	public void fetchUserToAccountFromGroup(String jobid) {
		List<JobTask> list = findUnfullJobtask(jobid);

		while (list.size() > 0) {
			for (JobTask jt : list) {
				RequestData data = new RequestData();
				data.setLimit(40 - jt.getUsernum() + 10);
				botService.collectUsersOfTask(data, jt.getId());
			}

			// 检查还有未满足的任务
			list = findUnfullJobtask(jobid);
		}
	}

	/**
	 * 查找用户未达到40的任务。
	 * 
	 * @param jobid
	 * @return
	 */
	private List<JobTask> findUnfullJobtask(String jobid) {
		JobTask jobTask = new JobTask();
		jobTask.setType("fetch");
		jobTask.setJobId(jobid);
		jobTask.setUsernum(40);
		jobTask.setStatus(JobTask.STATUS_NONE);//
		List<JobTask> list = findList(jobTask);
		logger.info("还有{}个账号用户数未达到40", list.size());
		return list;
	}

	/**
	 * 分配任务到指定数量账号。
	 * 
	 * @param jobid
	 *            任务id
	 * @param accountNum
	 *            账号数量
	 * @param data
	 */
	public void dispatchUserToAccount(String jobid, int accountNum,
			RequestData data) {
		String msg = "";
		// 从账户表中查询最多num条记录
		Account account = new Account();
		account.setLimit(accountNum);
		account.setJobid(jobid);
		List<Account> alist = accountService.findAccountForJob(account);
		if (alist.size() < accountNum) {
			accountNum = alist.size();
			msg = msg + " 只有" + accountNum
					+ "个账号运行中,全部提交运行。需要再启动更多的账号,才能满足需求！！";
		}

		// 遍历账号，从自己储备用户用获取40个账号
		for (Account ac : alist) {
			JobUser jobUser = new JobUser();
			jobUser.setJobId("auto");// 储备用户关联的job
			jobUser.setAccount(ac.getId());
			jobUser.setToJobid(jobid);// 限定目标job
			List<JobUser> users = jobUserService.findDistinctForJob(jobUser);

			// add job task
			JobTask jobTask = new JobTask();
			jobTask.setIsNewRecord(true);
			jobTask.preInsert();
			jobTask.setType("fetch");
			jobTask.setJobId(jobid);
			jobTask.setAccount(ac.getId());
			jobTask.setUsernum(users.size());
			jobTask.setStatus(JobTask.STATUS_NONE);// 未抽取
			save(jobTask);

			// 拷贝用户到jobid中
			for (JobUser ju : users) {
				JobUser u = new JobUser();
				u.setIsNewRecord(true);
				u.preInsert();

				u.setJobId(jobid);// 指定jobid
				u.setAccount(ju.getAccount());
				u.setFromGroup(ju.getFromGroup());
				u.setUserid(ju.getUserid());
				u.setUsername(ju.getUsername());
				u.setUserHash(ju.getUserHash());
				u.setStatus("0");

				jobUserService.save(u);
			}
		}

	}

	public JSONObject getRpcCallInfo(String taskid) {
		JobTask jobTask = new JobTask(taskid);
		return this.dao.getRpcCallInfo(jobTask);
	}

	public JSONObject findJobTaskStatsData(String jobId) {
		JobTask jobTask = new JobTask();
		jobTask.setJobId(jobId);
		return this.dao.findJobTaskStatsData(jobTask);
	}
}