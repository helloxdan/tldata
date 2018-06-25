package com.thinkgem.jeesite.modules.tl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Scheduled(cron = "0 0/10 * * * ?")
	@Transactional(readOnly = false)
	public void scheduleUpdateGroupInfo() {
		logger.info("定时调度，更新群组的link和用户数量……");
	}
}
