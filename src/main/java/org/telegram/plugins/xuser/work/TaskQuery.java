package org.telegram.plugins.xuser.work;

import org.telegram.plugins.xuser.XUserBot;

public interface TaskQuery {

	TaskData getTaskData(String jobid);

	void deleteTaskData(XUserBot bot, TaskData data);

}
