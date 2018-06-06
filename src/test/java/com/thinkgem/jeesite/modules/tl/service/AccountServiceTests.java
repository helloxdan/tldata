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
import com.thinkgem.jeesite.modules.tl.entity.Account; 

/**
 * 登录账号Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class AccountServiceTests extends BaseServiceTests {
	
	@Autowired
	private AccountService accountService;

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
		Account account =accountService.get(id);
		System.out.println(account);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 Account account =new Account();
		 //account.setId("1");
		 //account.setName("测试内容");
		 accountService.save(account);
	}
	
	@Test
	public void testFindList() throws Exception {
		Account account =new Account();
		 //account.setId("1");
		 //account.setName("测试内容");
		List<Account> list = accountService.findList(account);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<Account> page = new Page<Account>(1, 30);
		Account account = new Account();
		//account.setId("1");
		//account.setName("测试内容");
		Page<Account> pageList = accountService.findPage(page, account);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		Account account = new Account();
		account.setId(id);
		accountService.delete(account);
		
		Account account2 =accountService.get(id);
		Assert.isNull(account2);
	}
}