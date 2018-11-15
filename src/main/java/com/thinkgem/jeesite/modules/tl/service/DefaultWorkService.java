package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.XUtils;
import org.telegram.plugins.xuser.ex.ForbiddenGroupException;
import org.telegram.plugins.xuser.work.BotWrapper;
import org.telegram.plugins.xuser.work.TaskData;
import org.telegram.plugins.xuser.work.WorkService;
import org.telegram.tl.TLVector;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.utils.Constants;
import com.thinkgem.jeesite.modules.utils.JobCacheUtils;

@Service
public class DefaultWorkService implements WorkService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Logger slog = LoggerFactory.getLogger("com.telegram.success");
	// @Autowired
	// private JobTaskService jobTaskService;
	// 模拟运行的开关
	// boolean demo = true;
	public static boolean demo = Boolean.getBoolean(Global
			.getConfig("tl.work.demo"));

	Map<String, Integer> chatIdMap = Maps.newHashMap();
	Map<String, Long> chatAccessMap = Maps.newHashMap();

	static Set<String> extWords = new HashSet<String>();
	static {
		extWords.add("拉人");
		extWords.add("电报");
		extWords.add("用户");
		extWords.add("股票");
		extWords.add("微信");
		extWords.add("私聊");
		extWords.add("出售");
		extWords.add("管理");
		extWords.add("客服");
	}

	public static void setExtWords(String words) {
		if (StringUtils.isBlank(words))
			return;

		words = words.replace("，", ",");
		words = words.replace("、", ",");
		String[] ewords = words.split(",");
		for (String w : ewords) {
			extWords.add(w);
		}
	}

	public static void main(String[] args) {
		DefaultWorkService.setExtWords("管里，股票，微信、客戶");
		String name = "223管4客戶拉";
		boolean c = false;
		for (String w : extWords) {
			if (name.contains(w)) {
				System.out.println(name + "{}包含非法字符【" + w + "】");
				c = true;
				break;
			}
		}
	}

	@Override
	public List<JobUser> collectUsers(XUserBot bot, TaskData data) {
		List<JobUser> list = new ArrayList<JobUser>();
		if (demo) {
			mockCollectUser(bot, data, list);
		} else {
			// TODO
			String phone = bot.getPhone();
			int chatId = 0;
			long accessHash = 0;
			String key = phone + data.getSrcGroupUrl();
			if (chatIdMap.containsKey(key)) {
				chatId = chatIdMap.get(key);
				accessHash = chatAccessMap.get(key);
			} else {
				// 加入群组
				JSONObject json = bot.importInvite(data.getSrcGroupUrl());
				Boolean success = json.getBoolean("success");
				if (success) {
					chatId = json.getIntValue("chatid");
					accessHash = json.getLongValue("accessHash");
					chatIdMap.put(key, chatId);
					chatAccessMap.put(key, accessHash);
				} else {
					chatId = 0;
					String errorMsg = json.getString("msg");
					// 加入群组失败
					logger.warn("{},加入群组{}，失败,{}", phone,
							data.getSrcGroupName(), json.getString("msg"));

					// 如果加入群组失败，检查失败原因，停采从该群采集
					if (errorMsg.contains("FLOOD_WAIT_")) {
						int delay = Integer.parseInt(errorMsg
								.substring("FLOOD_WAIT_".length()));
						try {
							logger.warn("{},线程停止运行{}秒", phone, delay);
							Thread.sleep(delay * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (chatId == 0) {
				logger.warn("{},无群组信息，无法采集，{}", phone, data.getSrcGroupUrl());
			} else {
				TLVector<TLAbsUser> users = bot.collectUsers(chatId,
						accessHash, data.getOffset(), data.getLimit());
				logger.info("采集结果：job={}，account={},offset={},size={},{}",
						bot.getJobid(), phone, data.getOffset(), users.size(),
						data.getSrcGroupName());

				// 如果采集不到用户，说明已经到达上限10000或者超出用户数量
				if (users.size() == 0) {
					throw new ForbiddenGroupException(
							"采集用户数为0，可能已经达到采集上限10000或者超出群组的总用户数");
				}

				int num = 0;
				// 将数据存储到数据库
				for (TLAbsUser tluser : users) {
					TLUser u = (TLUser) tluser;

					if (u.isSelf() || u.isBot() || u.isDeleted() || u.isRestricted()
							|| u.isVerified()) {
						logger.debug(
								"忽略用户{},{}，isBot={},isDeleted={},isRestricted={},isVerified={}",
								u.getId(), u.getUserName(), u.isBot(),
								u.isDeleted(), u.isRestricted(), u.isVerified());
						continue;
					}

//					if (StringUtils.isBlank(u.getUserName())) {
//						logger.debug("用户没有username，忽略");
//						continue;
//					}

					String firstName = XUtils.transChartset(u.getFirstName());
					String lastName = XUtils.transChartset(u.getLastName());
					String name = firstName + lastName;
					if (name != null
							&& (name.length() > 20 || containForbiddenChar(name))) {
						logger.debug("用户名长度大于20，存在  拉人  电报群、用户 字样，忽略");
						continue;
					}

					// FIXEM 能判断用户是否已经加过是最好了，数据有点大，用其它缓存才行
					if (JobCacheUtils.existsJobUser(bot.getJobid(),
							"" + u.getId())) {
						//
						logger.info("用户{}已经存在~", u.getUserName());
						continue;
					}

					JobUser ju = new JobUser();
					ju.setUserid(u.getId() + "");
					ju.setUsername(u.getUserName());
					ju.setUserHash(u.getAccessHash());
					ju.setAccount(bot.getPhone());
					ju.setJobId(bot.getJobid());
					ju.setFirstname(firstName);
					ju.setLastname(lastName);
					ju.setFromGroup(data.getSrcGroupUrl());
					ju.setFromGroupName(data.getSrcGroupUrl());
					list.add(ju);
					num++;
				}
			}
		}
		return list;
	}

	private boolean containForbiddenChar(String name) {
		// boolean c = name.contains("拉人") || name.contains("电报群")
		// || name.contains("用户") || name.contains("股票")
		// || name.contains("微信") || name.contains("私聊")
		// || name.contains("出售") || name.contains("管理");
		boolean c = false;
		for (String w : extWords) {
			if (name.contains(w)) {
				logger.debug("{}包含非法字符【{}】", name, w);
				c = true;
				break;
			}
		}
		return c;
	}

	@Override
	public int inviteUsers(XUserBot bot, TaskData data, List<JobUser> users) {
		// TODO Auto-generated method stub
		int updateNum = 0;
		if (demo) {
			updateNum = RandomUtils.nextInt(1, users.size());
			System.err.println(bot.getPhone() + ",模拟拉人操作！~~~~~~~~~~~~~~~~~~~~"
					+ updateNum + ",total=" + BotWrapper.getTotal());
		} else {
			// TODO

			String phone = bot.getPhone();
			int chatId = 0;
			long accessHash = 0;
			// 目标群组
			String key = phone + data.getDestGroupUrl();
			if (chatIdMap.containsKey(key)) {
				chatId = chatIdMap.get(key);
				accessHash = chatAccessMap.get(key);
			} else {
				// 加入群组
				JSONObject json = bot.importInvite(data.getDestGroupUrl());
				Boolean success = json.getBoolean("success");
				if (success) {
					chatId = json.getIntValue("chatid");
					accessHash = json.getLongValue("accessHash");
					chatIdMap.put(key, chatId);
					chatAccessMap.put(key, accessHash);
				} else {
					chatId = 0;
					// 加入群组失败
					logger.warn("{},加入群组{}，失败,{}", phone,
							data.getSrcGroupName(), json.getString("msg"));
				}
			}
			if (chatId == 0) {
				logger.warn("{},无目标群组信息，无法加人", phone);
			} else {
				chatId = chatIdMap.get(key);
				accessHash = chatAccessMap.get(key);
				updateNum = bot.addUsers(chatId, accessHash, users);

				slog.info("{}，拉人：{},成功 {}，{}->{}", phone, users.size(),
						updateNum, data.getSrcGroupUrl(),
						data.getDestGroupUrl());
			}
		}
		return updateNum;
	}

	int mockTotal = 0;

	private void mockCollectUser(XUserBot bot, TaskData data, List<JobUser> list) {
		System.err.println(bot.getPhone() + ",模拟采集用户," + data.getSrcGroupUrl()
				+ ",offset=" + data.getOffset() + "~~~~~~~~~~~~~~~~~~~~");
		// TODO Auto-generated method stub
		int size = RandomUtils.nextInt(1, Constants.FETCH_PAGE_SIZE);
		mockTotal = mockTotal + size;
		if (mockTotal >= 150) {
			throw new ForbiddenGroupException(
					"mock,采集用户数为0，可能已经达到采集上限10000或者超出群组的总用户数");
		}
		for (int i = 0; i < size; i++) {
			JobUser u = new JobUser();
			u.setId("" + RandomUtils.nextInt(100000, 999999));
			u.setAccount(bot.getPhone());
			u.setJobId(bot.getJobid());
			u.setFirstname("xu");
			u.setLastname("test");
			u.setFromGroup(data.getSrcGroupUrl());
			u.setFromGroupName(data.getSrcGroupUrl());
			list.add(u);

			// FIXEM 能判断用户是否已经加过是最好了，数据有点大，用其它缓存才行
			if (JobCacheUtils.existsJobUser(bot.getJobid(), "" + u.getId())) {
				//
				logger.info("用户{}已经存在~", u.getFirstname() + u.getLastname());
				continue;
			}
		}
	}

	@Override
	public void destroy(XUserBot bot, TaskData data) {
		String key = bot.getPhone() + data.getDestGroupUrl();
		chatIdMap.remove(key);
		chatAccessMap.remove(key);
		key = bot.getPhone() + data.getSrcGroupUrl();
		chatIdMap.remove(key);
		chatAccessMap.remove(key);
	}
}
