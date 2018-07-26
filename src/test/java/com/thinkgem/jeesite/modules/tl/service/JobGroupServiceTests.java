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
import com.thinkgem.jeesite.modules.tl.entity.JobGroup; 

/**
 * 任务采集群组列表Service
 * @author admin
 * @version 2018-07-25
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class JobGroupServiceTests extends BaseServiceTests {
	
	@Autowired
	private JobGroupService jobGroupService;

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
		JobGroup jobGroup =jobGroupService.get(id);
		System.out.println(jobGroup);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 JobGroup jobGroup =new JobGroup();
		 //jobGroup.setId("1");
		 //jobGroup.setName("测试内容");
		 jobGroupService.save(jobGroup);
	}
	
	@Test
	public void testFindList() throws Exception {
		JobGroup jobGroup =new JobGroup();
		 //jobGroup.setId("1");
		 //jobGroup.setName("测试内容");
		List<JobGroup> list = jobGroupService.findList(jobGroup);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<JobGroup> page = new Page<JobGroup>(1, 30);
		JobGroup jobGroup = new JobGroup();
		//jobGroup.setId("1");
		//jobGroup.setName("测试内容");
		Page<JobGroup> pageList = jobGroupService.findPage(page, jobGroup);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		JobGroup jobGroup = new JobGroup();
		jobGroup.setId(id);
		jobGroupService.delete(jobGroup);
		
		JobGroup jobGroup2 =jobGroupService.get(id);
		Assert.isNull(jobGroup2);
	}
}