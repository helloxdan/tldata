package org.telegram.plugins.xuser.work;

import java.util.List;

import org.telegram.plugins.xuser.XUserBot;

import com.thinkgem.jeesite.modules.tl.entity.JobUser;

public interface WorkService {

	List<JobUser> collectUsers(XUserBot bot, TaskData data);

	int inviteUsers(XUserBot bot, TaskData data, List<JobUser> users);

	void destroy(XUserBot bot);

}
