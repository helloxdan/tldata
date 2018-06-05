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
import com.thinkgem.jeesite.modules.tl.entity.JobUser; 

/**
 * 邀请用户Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class JobUserServiceTests extends BaseServiceTests {
	
	@Autowired
	private JobUserService jobUserService;

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
		JobUser jobUser =jobUserService.get(id);
		System.out.println(jobUser);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 JobUser jobUser =new JobUser();
		 //jobUser.setId("1");
		 //jobUser.setName("测试内容");
		 jobUserService.save(jobUser);
	}
	
	@Test
	public void testFindList() throws Exception {
		JobUser jobUser =new JobUser();
		 //jobUser.setId("1");
		 //jobUser.setName("测试内容");
		List<JobUser> list = jobUserService.findList(jobUser);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<JobUser> page = new Page<JobUser>(1, 30);
		JobUser jobUser = new JobUser();
		//jobUser.setId("1");
		//jobUser.setName("测试内容");
		Page<JobUser> pageList = jobUserService.findPage(page, jobUser);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		JobUser jobUser = new JobUser();
		jobUser.setId(id);
		jobUserService.delete(jobUser);
		
		JobUser jobUser2 =jobUserService.get(id);
		Assert.isNull(jobUser2);
	}
}