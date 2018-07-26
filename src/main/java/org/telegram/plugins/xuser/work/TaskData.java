package org.telegram.plugins.xuser.work;

/**
 * 任务数据封装
 * 
 * @author ThinkPad
 *
 */
public class TaskData {
	String taskid;
	/**
	 * 待拉人群组ling
	 */
	String destGroupUrl;

	String srcGroupUrl;
	String srcGroupName;
	int offset;
	int limit;

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getDestGroupUrl() {
		return destGroupUrl;
	}

	public void setDestGroupUrl(String destGroupUrl) {
		this.destGroupUrl = destGroupUrl;
	}

	public String getSrcGroupUrl() {
		return srcGroupUrl;
	}

	public void setSrcGroupUrl(String srcGroupUrl) {
		this.srcGroupUrl = srcGroupUrl;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSrcGroupName() {
		return srcGroupName;
	}

	public void setSrcGroupName(String srcGroupName) {
		this.srcGroupName = srcGroupName;
	}

}
