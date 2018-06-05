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
import com.thinkgem.jeesite.modules.tl.entity.TlUser; 

/**
 * 好友用户Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class TlUserServiceTests extends BaseServiceTests {
	
	@Autowired
	private TlUserService tlUserService;

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
		TlUser tlUser =tlUserService.get(id);
		System.out.println(tlUser);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 TlUser tlUser =new TlUser();
		 //tlUser.setId("1");
		 //tlUser.setName("测试内容");
		 tlUserService.save(tlUser);
	}
	
	@Test
	public void testFindList() throws Exception {
		TlUser tlUser =new TlUser();
		 //tlUser.setId("1");
		 //tlUser.setName("测试内容");
		List<TlUser> list = tlUserService.findList(tlUser);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<TlUser> page = new Page<TlUser>(1, 30);
		TlUser tlUser = new TlUser();
		//tlUser.setId("1");
		//tlUser.setName("测试内容");
		Page<TlUser> pageList = tlUserService.findPage(page, tlUser);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		TlUser tlUser = new TlUser();
		tlUser.setId(id);
		tlUserService.delete(tlUser);
		
		TlUser tlUser2 =tlUserService.get(id);
		Assert.isNull(tlUser2);
	}
}