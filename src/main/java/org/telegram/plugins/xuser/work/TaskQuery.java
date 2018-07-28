package org.telegram.plugins.xuser.work;

import org.telegram.plugins.xuser.XUserBot;

public interface TaskQuery {

	TaskData getTaskData(String jobid,String phone);

	void deleteTaskGroup(XUserBot bot, TaskData data);

}
