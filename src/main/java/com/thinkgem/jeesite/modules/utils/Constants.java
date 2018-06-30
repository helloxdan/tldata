/**
 * 
 */
package com.thinkgem.jeesite.modules.utils;

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
	
	public static final int PER_SIZE=50;//tl 每天最多拉40人，但很多时候给的用户加不上，只能给的值比40大

	public static final double PER_SIZE_DOUBLE=50D;
}
