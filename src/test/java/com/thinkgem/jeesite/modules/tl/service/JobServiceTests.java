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
import com.thinkgem.jeesite.modules.tl.entity.Job; 

/**
 * 工作任务Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class JobServiceTests extends BaseServiceTests {
	
	@Autowired
	private JobService jobService;

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
		String id="1";
		Job job =jobService.get(id);
		System.out.println(job);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 Job job =new Job();
		 //job.setId("1");
		 //job.setName("测试内容");
		 jobService.save(job);
	}
	
	@Test
	public void testFindList() throws Exception {
		Job job =new Job();
		 //job.setId("1");
		 //job.setName("测试内容");
		List<Job> list = jobService.findList(job);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<Job> page = new Page<Job>(1, 30);
		Job job = new Job();
		//job.setId("1");
		//job.setName("测试内容");
		Page<Job> pageList = jobService.findPage(page, job);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		Job job = new Job();
		job.setId(id);
		jobService.delete(job);
		
		Job job2 =jobService.get(id);
		Assert.isNull(job2);
	}
}