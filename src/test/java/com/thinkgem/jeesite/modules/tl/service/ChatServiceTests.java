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
import com.thinkgem.jeesite.modules.tl.entity.Chat; 

/**
 * 群组会话记录Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class ChatServiceTests extends BaseServiceTests {
	
	@Autowired
	private ChatService chatService;

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
		Chat chat =chatService.get(id);
		System.out.println(chat);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 Chat chat =new Chat();
		 //chat.setId("1");
		 //chat.setName("测试内容");
		 chatService.save(chat);
	}
	
	@Test
	public void testFindList() throws Exception {
		Chat chat =new Chat();
		 //chat.setId("1");
		 //chat.setName("测试内容");
		List<Chat> list = chatService.findList(chat);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<Chat> page = new Page<Chat>(1, 30);
		Chat chat = new Chat();
		//chat.setId("1");
		//chat.setName("测试内容");
		Page<Chat> pageList = chatService.findPage(page, chat);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		Chat chat = new Chat();
		chat.setId(id);
		chatService.delete(chat);
		
		Chat chat2 =chatService.get(id);
		Assert.isNull(chat2);
	}
}