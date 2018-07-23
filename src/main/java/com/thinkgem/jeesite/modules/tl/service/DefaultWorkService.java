package com.thinkgem.jeesite.modules.tl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.plugins.xuser.XUserBot;
import org.telegram.plugins.xuser.work.TaskData;
import org.telegram.plugins.xuser.work.WorkService;

import com.thinkgem.jeesite.modules.tl.entity.JobUser;

@Service
public class DefaultWorkService implements WorkService{
	@Autowired
	private JobTaskService jobTaskService;

	@Override
	public List<JobUser> collectUsers(XUserBot bot, TaskData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void inviteUsers(XUserBot bot, TaskData data, List<JobUser> users) {
		// TODO Auto-generated method stub
		
	}

}
