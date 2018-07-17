package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.thinkgem.jeesite.common.test.BaseServiceTests;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;

@ContextConfiguration(locations = { "/test-spring-context.xml" })
public class JsSmsCardServiceTests extends BaseServiceTests {
	@Autowired
	private SmsCardService smsCardService;

	@Before
	public void setup() {
		String username = "admin";
		String password = "admin";
//		String password = "admin@xu";
		login(username, password);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testGetPhoneList() throws Exception {
		try {
			List<String> list = smsCardService.getPhoneList();
			System.out.println(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
