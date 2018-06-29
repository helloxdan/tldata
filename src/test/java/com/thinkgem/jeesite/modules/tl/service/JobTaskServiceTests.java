/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.test.BaseServiceTests;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * 调度任务Service
 * 
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class JobTaskServiceTests extends BaseServiceTests {

	@Autowired
	private JobTaskService jobTaskService;

	@Test
	public void testAddTasks() throws Exception {
		try {
			String id = "1";
			RequestData data = new RequestData();
			data.setType("task");
			data.setJobid("20180629");
			data.setUrl("https://t.me/silkchainchinese01");
			data.setNum(2);
			String msg = jobTaskService.addTasks(data);
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() {
		String username = "admin";
		String password = "admin";
		login(username, password);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGet() throws Exception {
		String id = "1";
		JobTask jobTask = jobTaskService.get(id);
		System.out.println(jobTask);
	}

	@Test
	public void testSave() throws Exception {
		JobTask jobTask = new JobTask();
		// jobTask.setId("1");
		// jobTask.setName("测试内容");
		jobTaskService.save(jobTask);
	}

	@Test
	public void testFindList() throws Exception {
		JobTask jobTask = new JobTask();
		// jobTask.setId("1");
		// jobTask.setName("测试内容");
		List<JobTask> list = jobTaskService.findList(jobTask);
		System.out.println(JsonMapper.toJsonString(list));
	}

	@Test
	public void testFindPage() throws Exception {
		Page<JobTask> page = new Page<JobTask>(1, 30);
		JobTask jobTask = new JobTask();
		// jobTask.setId("1");
		// jobTask.setName("测试内容");
		Page<JobTask> pageList = jobTaskService.findPage(page, jobTask);
		System.out.println(JsonMapper.toJsonString(pageList));
	}

	@Test
	public void testDelete() throws Exception {
		String id = "1";
		JobTask jobTask = new JobTask();
		jobTask.setId(id);
		jobTaskService.delete(jobTask);

		JobTask jobTask2 = jobTaskService.get(id);
		Assert.isNull(jobTask2);
	}
}