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
import com.thinkgem.jeesite.modules.tl.entity.DifferencesData; 

/**
 * 差异数据Service
 * @author admin
 * @version 2018-06-02
 */
@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class DifferencesDataServiceTests extends BaseServiceTests {
	
	@Autowired
	private DifferencesDataService differencesDataService;

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
		DifferencesData differencesData =differencesDataService.get(id);
		System.out.println(differencesData);		 
	}
	
	@Test
	public void testSave() throws Exception {
		 DifferencesData differencesData =new DifferencesData();
		 //differencesData.setId("1");
		 //differencesData.setName("测试内容");
		 differencesDataService.save(differencesData);
	}
	
	@Test
	public void testFindList() throws Exception {
		DifferencesData differencesData =new DifferencesData();
		 //differencesData.setId("1");
		 //differencesData.setName("测试内容");
		List<DifferencesData> list = differencesDataService.findList(differencesData);
		System.out.println(JsonMapper.toJsonString(list));
	}
	
	@Test
	public void testFindPage() throws Exception {
		Page<DifferencesData> page = new Page<DifferencesData>(1, 30);
		DifferencesData differencesData = new DifferencesData();
		//differencesData.setId("1");
		//differencesData.setName("测试内容");
		Page<DifferencesData> pageList = differencesDataService.findPage(page, differencesData);
		System.out.println(JsonMapper.toJsonString(pageList));
	}
	
	@Test
	public void testDelete() throws Exception {
		String id="1";
		DifferencesData differencesData = new DifferencesData();
		differencesData.setId(id);
		differencesDataService.delete(differencesData);
		
		DifferencesData differencesData2 =differencesDataService.get(id);
		Assert.isNull(differencesData2);
	}
}