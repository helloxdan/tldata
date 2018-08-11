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
import org.springframework.web.client.RestTemplate;

/**
 * 易码。http://www.51ym.me/User/apidocs.html
 * 
 * @author ThinkPad
 *
 */
public class YmSmsCardService implements SmsCardService {
	private static final String GJDM = "86";

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RestTemplate restTemplate;

	// telegram
	String project = "B3244DD57208B76";
	String username;
	String password;

	String token = null;
	boolean run = true;
	// 每次获取手机号 数量
	int maxPhoneNum = 1;

	private String userid;

	private static Object lock = new Object();

	void stop() {
		this.run = false;
	}

	@Override
	public void setForbidden(String phone) {
		if (StringUtils.isBlank(phone)) {
			logger.warn("加入黑名单的手机号为空");
			return;
		}

		if (phone.startsWith(GJDM)) {
			phone = phone.substring(2);
		}

		String result = null;
		try {
			String url = "http://api.fxhyd.cn/UserInterface.aspx?action=addignore&token=%s&itemid=%s&mobile=%s";
			url = String.format(url, token, project, phone);
			logger.debug("拉黑url={}", url);
			result = restTemplate.getForObject(url, String.class);
			if (logger.isInfoEnabled()) {
				logger.info("{}加入黑名单结果：{}", phone, result);
			}
			if (result.startsWith("success")) {
				logger.info("加入黑名单成功");
			} else {
				// throw new RuntimeException("加入黑名单失败");
				logger.error("{}加入黑名单失败,{}", phone, result);
			}

		} catch (Exception e) {
			logger.error("{}加入黑名单失败,{}", phone, e.getMessage() == null ? "" : e
					.getMessage().substring(0, 20));
			// throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<String> getPhoneList() {
		List<String> list = new ArrayList<String>();
		if (!run)
			return list;
		String result = null;
		try {
			String token = getToken();
			// http://api.fxhyd.cn/UserInterface.aspx?action=getmobile&token=%s&itemid=%s
			String url = "http://api.fxhyd.cn/UserInterface.aspx?action=getmobile&token=%s&itemid=%s";
			url = String.format(url, token, project);
			result = restTemplate.getForObject(url, String.class);

			// 返回示例：
			if (logger.isInfoEnabled()) {
				logger.info("取号码结果：" + result);
			}
			if (result.startsWith("success")) {
				String phone = result.split("\\|")[1];
				list.add(GJDM + phone);
			} else {
				logger.error("获取号码失败," + result);
				throw new RuntimeException(result);
			}

		} catch (Exception e) {
			logger.error("取号码,{}", e.getMessage());
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
		
		String re="success|0073435121acc113bf08110731c738a18f4b0e55";
		String[] res = re.split("\\|");
		System.out.println(res[0]+","+res[1]);

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
		if (phone.startsWith(GJDM)) {
			phone = phone.substring(2);
		}

		String result = null;
		try {
			// http://api.fxhyd.cn/UserInterface.aspx?action=getsms&token=TOKEN&itemid=项目编号&mobile=手机号码&release=1
			String url = "http://api.fxhyd.cn/UserInterface.aspx?action=getsms&token=%s&itemid=%s&mobile=%s&release=1";
			url = String.format(url, token, project, phone);
			// logger.info("取验证码url={}",url);
			result = restTemplate.getForObject(url, String.class);
			// 收到短信：success|短信内容
			// 短信尚未到达：3001，应继续调用取短信接口，直到超时为止。
			// 请求失败：错误代码，请根据不同错误代码进行不同的处理。
			if (logger.isDebugEnabled()) {
				logger.debug("{}取验证码结果：{}", phone, result);
			}

			if (result.startsWith("success")) {
				String content = result.split("\\|")[1];
				logger.info("{}获取验证码：{}", phone, content);
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
				list.add(new String[] { GJDM + phone, code });
			} else if (result.contains("3001")) {
				//
				logger.error("{}未获取到验证码,{}", phone, result);
			} else {
				logger.error("{}获取验证码失败,{}", phone, result);
				throw new RuntimeException(result);
			}

		} catch (Exception e) {
			logger.error("取验证码失败：" + e.getMessage());
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
			synchronized (lock) {
				if (token == null) {
					token = login();
				}
			}

		}
		return token;
	}

	/**
	 * 登录。
	 */
	private String login() {
		// TODO Auto-generated method stub
		String re = null;
		String url = "http://api.fxhyd.cn/UserInterface.aspx?action=login&username=%s&password=%s";
		url = String.format(url, getUsername(), getPassword());
		try {
			re = restTemplate.getForObject(url, String.class);
			if (logger.isDebugEnabled()) {
				logger.debug("登录结果：", re);
			}
			// 登录成功：success|token
			if (re.startsWith("success")) {
				token = re.split("\\|")[1];
				// setUserid(ret.getString("UserID"));
				logger.info("登录易码短信平台成功，token={}", token);
			} else {
				token = null;
				logger.info("登录易码短信平台失败，error={}", re);
				throw new RuntimeException("登录易码短信平台失败");
			}

		} catch (Exception e) {
			logger.error("登录异常:" + e.getMessage());
			throw new RuntimeException("登录异常，" + e.getMessage());
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
		// try {
		// password = YmSmsCardService.encryption(password);
		// } catch (Exception e) {
		// logger.error("md5 密码失败", e);
		// }
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

	@Override
	public void freePhone(String phone) {
		if (StringUtils.isBlank(phone)) {
			logger.warn("手机号为空");
			return;
		}

		if (phone.startsWith(GJDM)) {
			phone = phone.substring(2);
		}

		String result = null;
		try {
			String url = "http://api.fxhyd.cn/UserInterface.aspx?action=release&token=%s&itemid=%s&mobile=%s";
			url = String.format(url, token, project, phone);
			logger.debug("释放url={}", url);
			result = restTemplate.getForObject(url, String.class);
			if (logger.isInfoEnabled()) {
				logger.info("释放手机号结果：" + result);
			}

			if (result.startsWith("success")) {
				logger.info("釋放手机号成功");
			} else {
				logger.error("{}釋放手机号失败,{}", phone, result);
			}

		} catch (Exception e) {
			logger.error("釋放手机号失败,{}", e.getMessage() == null ? "" : e
					.getMessage().substring(0, 20));
			// throw new RuntimeException(e.getMessage());
		}
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
