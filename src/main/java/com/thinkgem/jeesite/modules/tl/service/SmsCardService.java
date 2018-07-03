package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

/**
 * 手机卡商服务接口，主要用于获取手机号列表、获取验证码、标记黑名单。
 * 
 * @author ThinkPad
 *
 */
public interface SmsCardService {
	List<String> getPhoneList();

	List<String[]> getPhoneCodeList();

	void setForbidden(String phone);
}
