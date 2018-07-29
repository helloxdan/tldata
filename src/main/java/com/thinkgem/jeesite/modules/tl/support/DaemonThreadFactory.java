/**
 * 
 */
package com.thinkgem.jeesite.modules.tl.support;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author ThinkPad
 *
 */
public class DaemonThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable r) {
		  Thread t = Executors.defaultThreadFactory().newThread(r);
          t.setDaemon(true);//守护线程
          return t;
	}

}
