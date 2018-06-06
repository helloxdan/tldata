/**
 * Copyright &copy; 2017-2020 <a href="https://www.zakm.com">zakm</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.test.BaseControllerTests;
import com.thinkgem.jeesite.modules.tl.entity.JobTask; 

/**
 * 调度任务Controller
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-mvc.xml" })
public class JobTaskControllerTests extends BaseControllerTests {
	@Autowired
	JobTaskController jobTaskController;
	
	// 登录用户名和密码
	protected String username = "admin";
	protected String password = "admin";

	@Before
	public void setup() {
		// 设置测试Controller
		setupController(jobTaskController);

		// 登录
		login(username, password);
	}

	@Test
	@Rollback(true)
	public void getSave() throws Exception { 
		String url = "/a/tl/jobTask/save";
		JobTask jobTask = new JobTask();
		//jobTask.setId("1");//自定义id
		//jobTask.setIsNewRecord(true);// 修改默认为新对象的标识，和自定义id配合使用
		//jobTask.setName("测试内容");
		 

		// 发送POST请求
		ResultActions resultAction = post(url, JsonMapper.toJsonString(jobTask));// "{\"id\":\"1\",\"name\":\"测试内容\"}"
		// 返回200
		resultAction.andExpect(MockMvcResultMatchers.status().isOk());
		MvcResult mvcResult = resultAction.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		System.out.println("POST请求结果:" + result);
	}
	
	@Test
	public void get() throws Exception {
		String url = "/a/tl/jobTask/get";
		// 发送GET请求
		ResultActions resultAction = get(url, "id=1");
		// 返回200
		resultAction.andExpect(MockMvcResultMatchers.status().isOk());
		MvcResult mvcResult = resultAction.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		System.out.println("GET请求结果:" + result);
	}
}