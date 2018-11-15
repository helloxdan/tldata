package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.thinkgem.jeesite.modules.tl.ex.SmsLoginException;

/**
 * 极速卡商。http://www.js-yzm.com:9000/index.html
 * 
 * @author ThinkPad
 *
 */
public class JsSmsCardService implements SmsCardService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RestTemplate restTemplate;
	String token = null;
	boolean run = true;
	// 每次获取手机号 数量
	int maxPhoneNum = 1;
	String project="2596";
	String username;
	String password;

	void stop() {
		this.run = false;
	}

	@Override
	public void setForbidden(String phone) {
		if (StringUtils.isBlank(phone)) {
			logger.warn("加入黑名单的手机号为空");
			return;
		}

		if (phone.startsWith("86")) {
			phone = phone.substring(2);
		}

		String result = null;
		try {
			String url = "http://www.js-yzm.com:9180/service.asmx/Hmd2Str?token="
					+ getToken() + "&xmid=2596&hm=" + phone + "&sf=0";
			result = restTemplate.getForObject(url, String.class);
			if (logger.isInfoEnabled()) {
				logger.info("加入黑名单结果：" + result);
			}

			if (result == null)
				throw new RuntimeException("加入黑名单失败");

		} catch (RestClientException e) {
			logger.error("加入黑名单失败", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<String> getPhoneList() {
		List<String> list = new ArrayList<String>();
		if (!run)
			return list;
		String result = null;
		try {
			String url = "http://www.js-yzm.com:9180/service.asmx/GetHM2Str?token="
					+ getToken()
					+ "&xmid=2596&sl="
					+ maxPhoneNum
					+ "&lx=0&a1=&a2=&pk=&ks=0&rj=woshishui";
			result = restTemplate.getForObject(url, String.class);
			if (logger.isInfoEnabled()) {
				logger.info("取号码结果：" + result);
			}

			if (result == null)
				throw new RuntimeException("获取号码失败");

			if (result.startsWith("hm=")) {
				logger.info("获取号码：" + result);
				String[] ids = result.substring(3).split(",");
				for (String phone : ids) {
					list.add("86" + phone);
				}
			} else if ("-8".equals(result)) {
				logger.error("账号余额不足");
				stop();
			} else {
				logger.warn("取号码失败：" + result);
			}

		} catch (SmsLoginException e) {
			//登录异常，向上抛出
			throw e;
		} catch (Exception e) {
			logger.error("取号码", e);
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	public static void main(String[] args) {
		// 正则表达式，获取数字
		String regEx = "[^0-9]";// 匹配指定范围内的数字
		// Pattern是一个正则表达式经编译后的表现模式
		Pattern p = Pattern.compile(regEx);

		String result = "2018-07-05 16:15:21æ¶å° [mt]Your verification code is 58292";
		result = result.substring(20);
		// 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
		Matcher m = p.matcher(result);

		// 将输入的字符串中非数字部分用空格取代并存入一个字符串
		String string = m.replaceAll(" ").trim();

		// 以空格为分割符在讲数字存入一个字符串数组中
		// String[] strArr = string.split(" ");
		System.out.println(string);

		System.out.println("8613751872".substring(2));
	}

	@Override
	public List<String[]> getPhoneCode(String phone) {
		List<String[]> list = new ArrayList<String[]>();
		if (!run)
			return list;

		if (StringUtils.isBlank(phone)) {
			logger.warn("取验证码的手机号为空");
			return list;
		}
		if (phone.startsWith("86")) {
			phone = phone.substring(2);
		}

		String result = null;
		try {
			String url = "http://www.js-yzm.com:9180/service.asmx/GetYzm2Str?token="
					+ getToken() + "&xmid=2596&hm=" + phone + "&sf=0";
			result = restTemplate.getForObject(url, String.class);
			logger.info("{}取验证码结果：{}", phone, result);
			if (logger.isDebugEnabled()) {
				logger.debug("{}取验证码结果：{}", phone, result);
			}

			if (result == null)
				throw new RuntimeException("获取验证码失败");

			if (result.length() > 4) {
				logger.info("获取验证码：" + result);

				// 正则表达式，获取数字
				String regEx = "[^0-9]";// 匹配指定范围内的数字
				// Pattern是一个正则表达式经编译后的表现模式
				Pattern p = Pattern.compile(regEx);
				// 把前面的时间去掉
				result = result.substring(20);

				// 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
				Matcher m = p.matcher(result);

				// 将输入的字符串中非数字部分用空格取代并存入一个字符串
				String code = m.replaceAll(" ").trim();
				list.add(new String[] { "86" + phone, code });

			} else if ("-8".equals(result)) {
				logger.error("账号余额不足");
				stop();
			} else if ("1".equals(result)) {
				logger.debug("还没收到验证码,继续....");

			} else {

				logger.warn("获取验证码失败：" + result);
			}

		} catch (Exception e) {
			logger.error("取验证码", e);
			throw new RuntimeException(e.getMessage());
		}
		return list;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getToken() {
		if (token == null) {
			token = login();
		}
		return token;
	}

	/**
	 * 登录。
	 */
	private String login() {
		// TODO Auto-generated method stub
		String token = null;
		// String url =
		// "http://www.js-yzm.com:9180/service.asmx/UserLoginStr?name=woshishui&psw=jj07170316";
		String url = "http://www.js-yzm.com:9180/service.asmx/UserLoginStr?name="
				+ getUsername() + "&psw=" + getPassword();

		try {
			token = restTemplate.getForObject(url, String.class);
			if (logger.isDebugEnabled()) {
				logger.debug("登录结果：" + token.toString());
			}
			if (token != null) {
				if (token.length() == 32) {
					logger.info("登录极速短信平台成功，token={}", token);
				} else {
					token = null;
					logger.info("登录极速短信平台失败，token={}", token);
				}
			} else {
				token = null;
				logger.info("登录极速短信平台失败，token={}", token);
			}

		} catch (RestClientException e) {
			logger.error("登录异常异常", e);
			throw new SmsLoginException("登录异常，" + e.getMessage());
		}
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void freePhone(String phone) {
		// TODO Auto-generated method stub

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxPhoneNum() {
		return maxPhoneNum;
	}

	public void setMaxPhoneNum(int maxPhoneNum) {
		this.maxPhoneNum = maxPhoneNum;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	@Override
	public void freeAllPhone() {
		// TODO Auto-generated method stub
		
	}

}
