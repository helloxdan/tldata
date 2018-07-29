package com.thinkgem.jeesite.modules.sys.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextCleanupListener;
import org.springframework.web.context.WebApplicationContext;

import com.thinkgem.jeesite.modules.sys.service.SystemService;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (!SystemService.printKeyLoadMessage()){
			return null;
		}
		return super.initWebApplicationContext(servletContext);
	}
	
	/**
	 * Close the root web application context.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		System.out.println("==================================================");
		System.out.println("==================================================");
		System.out.println("=====================程序已关闭========================");
		System.out.println("==================================================");
		System.out.println("==================================================");
		//退出所有线程，本程序使用了多线程，经常有线程没退出的情况
		System.exit(0);
	}
}
