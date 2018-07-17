package com.thinkgem.jeesite.modules.tl.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.security.Digests;

/**
 * 60码卡商。http://www.60ma.net/apiwiki/apiwiki-cn.html
 * 
 * @author ThinkPad
 *
 */
public class M60SmsCardService implements SmsCardService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RestTemplate restTemplate;

	String username;
	String password;

	String token = null;
	boolean run = true;
	// 每次获取手机号 数量
	int maxPhoneNum = 1;

	private String userid;

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

		JSONObject result = null;
		try {
			String url = "http://sms.60ma.net/newsmssrv?cmd=getsms&encode=utf-8&userid="
					+ getUserid()
					+ "&userkey="
					+ getToken()
					+ "&telnum="
					+ phone + "&docks=B3244DD57208B76&dtype=json";
			logger.info("拉黑url={}",url);
			result = restTemplate.getForObject(url, JSONObject.class);
			if (logger.isInfoEnabled()) {
				logger.info("加入黑名单结果：" + result);
			}
			result=result.getJSONObject("Return");
			String staus = result.getString("Staus");
			if (!"0".equals(staus)) {
				// throw new RuntimeException("加入黑名单失败");
				logger.error("加入黑名单失败" + result.getString("ErrorInfo"));
			} else {
				logger.info("加入黑名单成功");
			}

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
		JSONObject result = null;
		try {
			String token=getToken();
			String url = "http://sms.60ma.net/newsmssrv?cmd=gettelnum&encode=utf-8&userid="
					+ getUserid()
					+ "&userkey="
					+ token
					+ "&docks=B3244DD57208B76&dtype=json";
			result = restTemplate.getForObject(url, JSONObject.class);

			// 返回示例：{"Return":{"Staus":"0","Telnum":"17011134058","ErrorInfo":"获取成功！}}
			if (logger.isInfoEnabled()) {
				logger.info("取号码结果：" + result);
			}
			result=result.getJSONObject("Return");
			String staus = result.getString("Staus");
			if (!"0".equals(staus)) {
				// throw new RuntimeException("获取号码失败"
				// + result.getString("ErrorInfo"));
				logger.error("获取号码失败," + result.getString("ErrorInfo"));
			} else {
				String phone = result.getString("Telnum");
				list.add("86" + phone);
			}

		} catch (RestClientException e) {
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

		try {
			String password = "123456";
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			password = new String(md5.digest(password.getBytes("utf-8")),
					"utf-8");
			System.out.println(password);
			System.out.println(Digests.md5("123456".getBytes("utf-8")));
			System.out.println(M60SmsCardService.encryption("123456"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String encryption(String plain) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plain.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;
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

		JSONObject result = null;
		try {
			String url = "http://sms.60ma.net/newsmssrv?cmd=getsms&encode=utf-8&userid="
					+ getUserid()
					+ "&userkey="
					+ getToken()
					+ "&telnum="
					+ phone + "&dockcode=B3244DD57208B76&dtype=json";
			logger.info("取验证码url={}",url); 
			result = restTemplate.getForObject(url, JSONObject.class);
			logger.info("{}取验证码结果：{}", phone, result);
			// 返回示例：{"Return":{"Staus":"0","SmsContent":"你正在注册微信帐号，验证码71408。请勿转发。【腾讯科技】,"ErrorInfo":"成功！}}
			if (logger.isDebugEnabled()) {
				logger.debug("{}取验证码结果：{}", phone, result);
			}
			
			result=result.getJSONObject("Return");
			String staus = result.getString("Staus");
			if (!"0".equals(staus)) {
				// throw new RuntimeException("获取验证码失败");
				logger.error("获取验证码失败：", result);
			} else {
				String content = result.getString("SmsContent");
				logger.info("获取验证码：" + content);
				// 正则表达式，获取数字
				String regEx = "[^0-9]";// 匹配指定范围内的数字
				// Pattern是一个正则表达式经编译后的表现模式
				Pattern p = Pattern.compile(regEx);
				// 把前面的时间去掉
				content = content.substring(10);

				// 一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
				Matcher m = p.matcher(content);

				// 将输入的字符串中非数字部分用空格取代并存入一个字符串
				String code = m.replaceAll(" ").trim();
				list.add(new String[] { "86" + phone, code });

			}

		} catch (RestClientException e) {
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
		JSONObject re = null;
		String url = "http://sms.60ma.net/loginuser?cmd=login&encode=utf-8&dtype=json&username="
				+ getUsername() + "&password=" + getPassword();

		try {
			re = restTemplate.getForObject(url, JSONObject.class);
			if (logger.isDebugEnabled()) {
				logger.debug("登录结果：", re);
			}

			JSONObject ret = re.getJSONObject("Return");
			String staus = ret.getString("Staus");
			if ("0".equals(staus)) {
				token = ret.getString("UserKey");
				setUserid(ret.getString("UserID"));
				logger.info("登录60码短信平台成功，re={}", re);
			} else {
				token = null;
				logger.info("登录60码短信平台失败，error={}", re.getString("ErrorInfo"));
			}

		} catch (Exception e) {
			logger.error("登录异常异常", e);
			throw new RuntimeException(e.getMessage());
		}
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		try {
			password = M60SmsCardService.encryption(password);
		} catch (Exception e) {
			logger.error("md5 密码失败", e);
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
