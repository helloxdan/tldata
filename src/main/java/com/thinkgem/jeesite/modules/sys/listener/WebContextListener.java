package com.thinkgem.jeesite.modules.sys.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.WebApplicationContext;

import com.thinkgem.jeesite.modules.sys.service.SystemService;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	static List<ExecutorService> executorServiceList=new ArrayList<ExecutorService>();
	
	public static void addExecutorService(ExecutorService es){
		executorServiceList.add(es);
	}
	
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
		
		for (ExecutorService es : executorServiceList) {
			try {
				es.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("==================================================");
		System.out.println("==================================================");
		System.out.println("=====================程序已关闭========================");
		System.out.println("==================================================");
		System.out.println("==================================================");
		//退出所有线程，本程序使用了多线程，经常有线程没退出的情况
		System.exit(0);
	}
}
