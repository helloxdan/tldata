/**
 * 
 */
package com.thinkgem.jeesite.modules.utils;

import com.thinkgem.jeesite.common.config.Global;

/**
 * 静态常量
 * 
 * @author ThinkPad
 *
 */
public class Constants {

	// 审核状态
	public static final String VERIFY_STATUS_TOVERIFY = "toverify";// 待审核
	public static final String VERIFY_STATUS_NORMAL = "normal";// 正常
	public static final String VERIFY_STATUS_NOTPASS = "notpass";// 审核不通过
	public static final String VERIFY_STATUS_EXPIRED = "expired";// 已过期
	
	public static final int USER_LIMIT_SIZE=50;//tl 每天最多拉40人，但很多时候给的用户加不上，只能给的值比40大
	public static final int FETCH_PAGE_SIZE = Integer.parseInt(Global.getConfig("tl.fetch.pagesize"));//150;

	public static final double PER_SIZE_DOUBLE=50D;
	
	public static final int MIN_CACHE_USER_NUM=200;//最低的储备用户数
	
	public static final int APIKEY = 202491; // your api key
	public static final String APIHASH = "9f32d44fca581599dbbe02cec25ffe58"; // your api hash
	public static final String ADMIN_ACCOUNT="";
}
