package com.thinkgem.jeesite.modules.sys.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.WebApplicationContext;

import com.thinkgem.jeesite.modules.sys.service.SystemService;

public class WebContextListener extends
		org.springframework.web.context.ContextLoaderListener {

	static List<ExecutorService> executorServiceList = new ArrayList<ExecutorService>();

	public static void addExecutorService(ExecutorService es) {
		executorServiceList.add(es);
	}

	@Override
	public WebApplicationContext initWebApplicationContext(
			ServletContext servletContext) {
		if (!SystemService.printKeyLoadMessage()) {
			return null;
		}
		return super.initWebApplicationContext(servletContext);
	}

	public static Thread[] findAllThreads() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;
		// traverse the ThreadGroup tree to the top
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}
		// Create a destination array that is about
		// twice as big as needed to be very confident
		// that none are clipped.
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		// Load the thread references into the oversized
		// array. The actual number of threads loaded
		// is returned.
		int actualSize = topGroup.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		return list;
	}

	/**
	 * Close the root web application context.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);

		Thread[] threads = findAllThreads();
		for (Thread thread : threads) {
			thread.interrupt();
		}

		for (ExecutorService es : executorServiceList) {
			try {
				es.shutdownNow();
				System.out.println("关闭线程");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out
				.println("==================================================");
		System.out
				.println("==================================================");
		System.out
				.println("=====================程序已关闭========================");
		System.out
				.println("==================================================");
		System.out
				.println("==================================================");
		// 退出所有线程，本程序使用了多线程，经常有线程没退出的情况
		System.exit(1);
	}
}
