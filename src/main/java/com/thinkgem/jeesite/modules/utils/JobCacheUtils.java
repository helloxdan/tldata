package com.thinkgem.jeesite.modules.utils;

import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.JedisUtils;

public class JobCacheUtils {
	private static String cacheName = "jobuserCache";
	
	public static boolean existsJobUser(String jobid, String userid) {
		boolean exists = false;
		String key = jobid + userid;
		if (get(key) == null) {
			//没有就存入缓存
			put(key, userid);
		} else {
			exists = true;
		}
		return exists;
	}

	private static Object get(String key) {
//		return CacheUtils.get(cacheName, key);
		return JedisUtils.get(key);
	}

	private static void put(String key, String obj) {
//		CacheUtils.put(cacheName, key,obj);
		JedisUtils.set(key, obj, 60*60*24);
	}
}
