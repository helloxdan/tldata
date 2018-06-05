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
import com.thinkgem.jeesite.modules.tl.entity.UserSession; 

/**
 * 用户会话Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class UserSessionServiceTests extends BaseServiceTests {
	
	@Autowired
	private UserSessionService userSessionService;

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
		UserSession userSession =userSessionService.get(id);
		System.out.println(userSession);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 UserSession userSession =new UserSession();
		 //userSession.setId("1");
		 //userSession.setName("测试内容");
		 userSessionService.save(userSession);
	}
	
	@Test
	public void testFindList() throws Exception {
		UserSession userSession =new UserSession();
		 //userSession.setId("1");
		 //userSession.setName("测试内容");
		List<UserSession> list = userSessionService.findList(userSession);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<UserSession> page = new Page<UserSession>(1, 30);
		UserSession userSession = new UserSession();
		//userSession.setId("1");
		//userSession.setName("测试内容");
		Page<UserSession> pageList = userSessionService.findPage(page, userSession);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		UserSession userSession = new UserSession();
		userSession.setId(id);
		userSessionService.delete(userSession);
		
		UserSession userSession2 =userSessionService.get(id);
		Assert.isNull(userSession2);
	}
}