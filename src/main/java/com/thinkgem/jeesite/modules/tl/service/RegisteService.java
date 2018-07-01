package com.thinkgem.jeesite.modules.tl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账号注册服务。
 * 
 * @author ThinkPad
 *
 */
@Service
@Transactional(readOnly = true)
public class RegisteService {
	@Autowired
	private BotService botService;

	/**
	 * 自动注册
	 */
	public void autoRegiste() {

	}

	/**
	 * 注册单个账号。
	 * 
	 * @param phone
	 */
	public void registe(String phone) {

	}
}
