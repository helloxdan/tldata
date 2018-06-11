/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
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

	/**
	 * 批量增加任务数。
	 * 
	 * @param data
	 * @return
	 */
	public String addTasks(RequestData data) {
		String msg = "";
		String type = data.getType();
		int num = data.getNum();

		if ("any".equals(type)) {
			// 用户来自不同群组，任意选择
			// TODO 优先从现有的用户中直接复制
		} else {
			// 需要制定群组
			if (StringUtils.isBlank(data.getUrl())) {
				throw new RuntimeException("来源群组url不能为空");
			}
			// 从账户表中查询‘运行中’状态的记录，最多num条记录
			Account account = new Account();
			account.setStatus(Account.STATUS_RUN);// 运行中状态
			List<Account> alist = accountService.findList(account);
			if (alist.size() < num) {
				num = alist.size();
				msg = msg + " 只有" + num + "个账号运行中,全部提交运行。需要再启动过多的账号！！";
			}

			// 只去固定数量的账号
			List<Account> list = new ArrayList<Account>();
			for (int i = 0; i < num; i++) {
				list.add(alist.get(i));
			}

			int offsetNum = data.getOffset();
			int limitNum = data.getLimit();
			int n = 0;
			// 按规则批量生成任务。
			for (Account ac : list) {
				// 总数超过10000，取消。每个群组最多拉取10000人
				if (offsetNum + limitNum > 10000) {
					msg = msg + " 拉取人数超过10000人，退出；";
					break;
				}

				JobTask jobTask = new JobTask();
				jobTask.setType("fetch");
				jobTask.setJobId(data.getJobid());
				jobTask.setAccount(ac.getId());
				jobTask.setGroupId(data.getChatId() + "");
				jobTask.setOffsetNum(offsetNum);
				jobTask.setLimitNum(limitNum);
				jobTask.setStatus(JobTask.STATUS_NONE);// 未抽取

				offsetNum = offsetNum + limitNum;

				save(jobTask);// 保存记录
			}
		}
		return msg;
	}

	public JSONObject getRpcCallInfo(String taskid) {
		JobTask jobTask = new JobTask(taskid);
		return this.dao.getRpcCallInfo(jobTask);
	}
}