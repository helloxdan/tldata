package com.thinkgem.jeesite.modules.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.XUtils;
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
	@Autowired
	private JobTaskService jobTaskService;
	// 模拟运行的开关
//	boolean demo = false;
	boolean demo =  Boolean.getBoolean(Global
			.getConfig("tl.work.demo"));;
	Map<String, Integer> chatIdMap = Maps.newHashMap();
	Map<String, Long> chatAccessMap = Maps.newHashMap();

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
					// 加入群组失败
					logger.warn("{},加入群组{}，失败,{}", phone, data.getSrcGroupName(), json.getString("msg"));
				}
			}
			if (chatId == 0) {
				logger.warn("{},无群组信息，无法采集", phone);
			} else {
				TLVector<TLAbsUser> users = bot.collectUsers(chatId, accessHash, data.getOffset(), data.getLimit());
				logger.info("采集结果：job={}，account={},size={},{}", bot.getJobid(), phone, users.size(),data.getSrcGroupName());

				int num = 0;
				// 将数据存储到数据库
				for (TLAbsUser tluser : users) {
					TLUser u = (TLUser) tluser;
					if (StringUtils.isBlank(u.getUserName())) {
						logger.debug("用户没有username，忽略");
						continue;
					}

					String firstName = XUtils.transChartset(u.getFirstName());
					String lastName = XUtils.transChartset(u.getLastName());
					if (firstName != null && ((firstName.length() > 100 || lastName.length() > 100
							|| (firstName.contains("拉人") || firstName.contains("电报群") || firstName.contains("用户"))))) {
						logger.debug("用户名长度大于100，存在  拉人  电报群、用户 字样，忽略");
						continue;
					}

					// FIXEM 能判断用户是否已经加过是最好了，数据有点大，用其它缓存才行
					if (JobCacheUtils.existsJobUser(bot.getJobid(), "" + u.getId())) {
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

	@Override
	public int inviteUsers(XUserBot bot, TaskData data, List<JobUser> users) {
		// TODO Auto-generated method stub
		int updateNum = 0;
		if (demo) {
			updateNum = RandomUtils.nextInt(1, users.size());
			System.err.println(bot.getPhone() + ",模拟拉人操作！~~~~~~~~~~~~~~~~~~~~" + updateNum+",total="+BotWrapper.getTotal());
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
					logger.warn("{},加入群组{}，失败,{}", phone, data.getSrcGroupName(), json.getString("msg"));
				}
			}
			if (chatId == 0) {
				logger.warn("{},无目标群组信息，无法加人", phone);
			} else {
				chatId = chatIdMap.get(key);
				accessHash = chatAccessMap.get(key);
				updateNum = bot.addUsers(chatId, accessHash, users);

				slog.info("{}，拉人：{},成功 {}，{}->{}",phone, users.size(), updateNum, data.getSrcGroupUrl(),
						data.getDestGroupUrl());
			}
		}
		return updateNum;
	}

	private void mockCollectUser(XUserBot bot, TaskData data, List<JobUser> list) {
		System.err.println(bot.getPhone() + ",模拟采集用户~~~~~~~~~~~~~~~~~~~~");
		// TODO Auto-generated method stub
		int size = RandomUtils.nextInt(1, Constants.FETCH_PAGE_SIZE);
		for (int i = 0; i < size; i++) {
			JobUser u = new JobUser();
			u.setAccount(bot.getPhone());
			u.setJobId(bot.getJobid());
			u.setFirstname("xu");
			u.setLastname("test");
			u.setFromGroup(data.getSrcGroupUrl());
			u.setFromGroupName(data.getSrcGroupUrl());
			list.add(u);
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
