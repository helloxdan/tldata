package com.thinkgem.jeesite.modules.utils;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.JedisUtils;

public class JobCacheUtils {
	private static String cacheName = "jobuserCache";
	private static boolean redisCache = Boolean.getBoolean(Global
			.getConfig("tl.cache.redis"));

	public static boolean existsJobUser(String jobid, String userid) {
		boolean exists = false;
		String key = jobid + userid;
		if (get(key) == null) {
			// 没有就存入缓存
			put(key, userid);
		} else {
			exists = true;
		}
		return exists;
	}

	private static Object get(String key) {
		if (redisCache) {
			return JedisUtils.get(key);
		} else {
			return CacheUtils.get(cacheName, key);
		}
	}

	private static void put(String key, String obj) {
		if (redisCache) {
			JedisUtils.set(key, obj, 60 * 60 * 24);
		} else {
			CacheUtils.put(cacheName, key, obj);
		}
	}
}
