/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.vo.ReturnWrap;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.GroupService;
import com.thinkgem.jeesite.modules.tl.service.JobGroupService;
import com.thinkgem.jeesite.modules.tl.service.JobService;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * 工作任务Controller
 * 
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/job")
public class JobController extends BaseController {

	@Autowired
	private JobService jobService;
	@Autowired
	private JobGroupService jobGroupService;
	@Autowired
	private BotService botService;
	@Autowired
	private GroupService groupService;

	@RequestMapping(value = "/group/list")
	@ResponseBody
	public ReturnWrap groupList(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {

			JobGroup jobGroup = new JobGroup();
			jobGroup.setJobId(data.getId());
			List<JobGroup> list = jobGroupService.findList(jobGroup);
			result.setData(list);
		} catch (Exception e) {
			logger.error("取群组列表异常，", e);
			result.fail("取群组列表异常，" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/group/add")
	@ResponseBody
	public ReturnWrap addGroup(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String jobid = data.getId();
			String ids = data.getIds();
			if (StringUtils.isNotBlank(ids)) {
				String[] groupIds = ids.split(",");
				for (String groupId : groupIds) {
					Group group = groupService.get(groupId);

					// 插入job_group表
					JobGroup jobGroup=new JobGroup();
					jobGroup.setJobId(jobid);
					jobGroup.setGroupId(group.getId());
					jobGroup.setGroupName(group.getName());
					jobGroup.setGroupUrl(group.getUrl());
					jobGroup.setUsernum(group.getUsernum());
					jobGroup.setOffset(0);
					jobGroupService.save(jobGroup);
				}
			}
			result.success("Ok");
		} catch (Exception e) {
			logger.error("增加群组异常，", e);
			result.fail("增加群组异常，" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/group/del")
	@ResponseBody
	public ReturnWrap delGroup(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			JobGroup group = new JobGroup();
			group.setId(data.getId());
			jobGroupService.delete(group);
			result.success("删除成功");
		} catch (Exception e) {
			logger.error("删除群组异常，", e);
			result.fail("删除群组异常，" + e.getMessage());
		}
		return result;
	}

	@ModelAttribute
	public Job get(@RequestParam(required = false) String id) {
		Job entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = jobService.get(id);
		}
		if (entity == null) {
			entity = new Job();
		}
		return entity;
	}

	@RequiresPermissions("tl:job:view")
	@RequestMapping(value = { "list", "" })
	public String list(Job job, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<Job> page = jobService.findPage(new Page<Job>(request, response),
				job);
		model.addAttribute("page", page);
		return "modules/tl/jobList";
	}

	@RequiresPermissions("tl:job:view")
	@RequestMapping(value = "form")
	public String form(Job job, Model model) {
		if (job.getIsNewRecord()) {
			job.setId(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			job.setBoss("boss");
			job.setUsernum(100);
			job.setDay(1);
			job.setIsNewRecord(true);
		}
		model.addAttribute("job", job);
		return "modules/tl/jobForm";
	}

	@RequiresPermissions("tl:job:edit")
	@RequestMapping(value = "save")
	public String save(Job job, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, job)) {
			return form(job, model);
		}
		// if (job.getIsNewRecord()) {
		// 获取目标群组信息
		// if (StringUtils.isNotBlank(job.getGroupUrl())) {
		// JSONObject result =
		// botService.updateGroupInfoByLink(job.getGroupUrl());
		// job.setGroupId(result.getInteger("groupid"));
		// job.setGroupName(result.getString("name"));
		// }
		// }

		boolean isNew=job.getIsNewRecord();
		jobService.save(job);
		addMessage(redirectAttributes, "保存工作任务成功");
		if(isNew){
			job.setIsNewRecord(false);
			return form(job, model);	
		}else{
		return "redirect:" + Global.getAdminPath() + "/tl/job/?repage";
		}
	}

	@RequiresPermissions("tl:job:edit")
	@RequestMapping(value = "delete")
	public String delete(Job job, RedirectAttributes redirectAttributes) {
		jobService.delete(job);
		addMessage(redirectAttributes, "删除工作任务成功");
		return "redirect:" + Global.getAdminPath() + "/tl/job/?repage";
	}

	@RequiresPermissions("tl:job:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			jobService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}

}