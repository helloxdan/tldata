package com.thinkgem.jeesite.modules.tl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.plugins.xuser.XUserBot;

@Service
public class BotFactory {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 *工厂方法，方便模拟测试增加的类。
	 * 
	 * @return
	 */
	public XUserBot createBot() {
		return new XUserBot();
	}

}
