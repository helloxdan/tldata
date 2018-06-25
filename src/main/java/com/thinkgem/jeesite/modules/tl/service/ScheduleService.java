package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.plugins.xuser.IBot;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BotDataService botDataService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private JobService jobService;
	@Autowired
	private JobTaskService jobTaskService;
	@Autowired
	private JobUserService jobUserService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private BotService botService;
	/**
	 * 检查账号和群组的关系。如果发现有账号没有加入某个群组，则自动加入。
	 */
	// @Scheduled(cron = "0/10 * * * * ?")
	@Transactional(readOnly = false)
	public void scheduleUpdateGroupInfo() {
		logger.info("定时调度，更新群组的link和用户数量……");
		// TODO
		Group group = new Group();
		List<Group> list = groupService.findListWithoutUsernum(group);
		int num = 0;
		for (Group g : list) {
			// 暂时只执行两次，检查api接口是否支持连续执行
			if (num == 2)
				break;
			updateGroupInfo(g);
			num++;
		}
	}

	@Transactional(readOnly = false)
	public void updateGroupInfo(Group g) {
		// 通过管理员账号获取信息
		try {
			IBot bot = botService.getBotByPhone(botService.getAdminAccount());
			if (bot == null) {
				// throw new RuntimeException(botService.getAdminAccount()
				// + "账号实例不存在");
				RequestData data = new RequestData();
				data.setPhone(botService.getAdminAccount());
				botService.start(data);
				return;
			}
			JSONObject json = bot.getGroupInfo(Integer.parseInt(g.getId()), g.getAccesshash(), true);
//			String link = json.getString("link");
			Integer usernum = json.getInteger("usernum");
			// if (StringUtils.isNotBlank(link)) {
			Group group = groupService.get(g.getId());
			// group.setUrl(link);
			group.setUsernum(usernum);

			groupService.save(group);
			// } else {
			// logger.warn("通过账号{}无法获取群组【{}】的link", botService.getAdminAccount(),
			// g.getName());
			// }

		} catch (Exception e) {
			logger.error("更新群组的link地址异常", e);
		}
	}

	// @Scheduled(cron = "0 0/5 * * * ?")
	@Transactional(readOnly = false)
	public void scheduleJoinGroup() {
		logger.info("定时调度，账号自动加入有效群组……");

		// TODO
	}

	/**
	 * 定时抽取用户信息。
	 */
	// @Scheduled(cron = "0/5 * * * * ?")
	@Transactional(readOnly = false)
	public void scheduleFetchUser() {
		logger.info("定时调度，从群组抽取用户数据……");

		// TODO
	}
}
