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
import com.thinkgem.jeesite.modules.tl.entity.Group; 

/**
 * 群组Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class GroupServiceTests extends BaseServiceTests {
	
	@Autowired
	private GroupService groupService;

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
		Group group =groupService.get(id);
		System.out.println(group);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 Group group =new Group();
		 //group.setId("1");
		 //group.setName("测试内容");
		 groupService.save(group);
	}
	
	@Test
	public void testFindList() throws Exception {
		Group group =new Group();
		 //group.setId("1");
		 //group.setName("测试内容");
		List<Group> list = groupService.findList(group);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<Group> page = new Page<Group>(1, 30);
		Group group = new Group();
		//group.setId("1");
		//group.setName("测试内容");
		Page<Group> pageList = groupService.findPage(page, group);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		Group group = new Group();
		group.setId(id);
		groupService.delete(group);
		
		Group group2 =groupService.get(id);
		Assert.isNull(group2);
	}
}