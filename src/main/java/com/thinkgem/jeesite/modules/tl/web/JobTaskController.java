/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.web;

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
import org.telegram.plugins.xuser.work.BotWrapper;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.vo.ReturnWrap;
import com.thinkgem.jeesite.modules.tl.entity.Job;
import com.thinkgem.jeesite.modules.tl.entity.JobTask;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.JobService;
import com.thinkgem.jeesite.modules.tl.service.JobTaskService;
import com.thinkgem.jeesite.modules.tl.service.RegistePoolService;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * 调度任务Controller
 * 
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/jobTask")
public class JobTaskController extends BaseController {

	@Autowired
	private JobTaskService jobTaskService;
	@Autowired
	private RegistePoolService registePoolService;
	
	@Autowired
	private JobService jobService;
	@Autowired
	private BotService botService;

	@RequestMapping(value = "/addTasks")
	@ResponseBody
	public ReturnWrap addTasks(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			if (StringUtils.isNotBlank(data.getUrl())
					&& !"any".equals(data.getType())) {
				// 根据群组链接获取群组id
				JSONObject json = botService.updateGroupInfoByLink(data
						.getUrl());
				if (json.getInteger("groupid") != null) {
					data.setChatId(json.getInteger("groupid"));
				} else {
					throw new RuntimeException("找不到" + data.getUrl() + "对应的群组");
				}
			}

			String msg = jobTaskService.addTasks(data);
			result.setData(msg);
		} catch (Exception e) {
			logger.error("新增任务异常，",e);
			result.fail("新增任务异常，" + e.getMessage());
		}
		return result;
	}

	@ModelAttribute
	public JobTask get(@RequestParam(required = false) String id) {
		JobTask entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = jobTaskService.get(id);
		}
		if (entity == null) {
			entity = new JobTask();
		}
		return entity;
	}

	/**
	 * 分配任务
	 * 
	 * @param jobTask
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("tl:jobTask:view")
	@RequestMapping(value = "dispatch")
	public String dispatch(JobTask jobTask, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 任务信息
		Job job=jobService.get(jobTask.getJobId());
		jobTask.setJob(job);

		Page<JobTask> page = jobTaskService.findPage(new Page<JobTask>(request,
				response), jobTask);

		// 查询共任务数和有效用户数
		JSONObject data=jobTaskService.findJobTaskStatsData(jobTask.getJobId());
		model.addAttribute("jobTaskStats", data);
		model.addAttribute("job", job);
		model.addAttribute("regRunStatus", registePoolService.isStart());
		model.addAttribute("successTotal", BotWrapper.getTotal());
		model.addAttribute("phonePlanNum", registePoolService.getPlanSize());
		model.addAttribute("phoneSuccessNum", registePoolService.getSuccessSize());
		
	 
		
		
		model.addAttribute("page", page);
		return "modules/tl/jobTaskDispatch";
	}

	@RequiresPermissions("tl:jobTask:view")
	@RequestMapping(value = { "list", "" })
	public String list(JobTask jobTask, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<JobTask> page = jobTaskService.findPage(new Page<JobTask>(request,
				response), jobTask);
		model.addAttribute("page", page);
		return "modules/tl/jobTaskList";
	}

	@RequiresPermissions("tl:jobTask:view")
	@RequestMapping(value = "form")
	public String form(JobTask jobTask, Model model) {
		model.addAttribute("jobTask", jobTask);
		return "modules/tl/jobTaskForm";
	}

	@RequiresPermissions("tl:jobTask:edit")
	@RequestMapping(value = "save")
	public String save(JobTask jobTask, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jobTask)) {
			return form(jobTask, model);
		}
		jobTaskService.save(jobTask);
		addMessage(redirectAttributes, "保存调度任务成功");
		if ("dispatch".equals(jobTask.getAction())) {
			return "redirect:" + Global.getAdminPath()
					+ "/tl/jobTask/dispatch?jobId=" + jobTask.getJobId();
		} else {
			return "redirect:" + Global.getAdminPath() + "/tl/jobTask/?repage";
		}
	}

	@RequiresPermissions("tl:jobTask:edit")
	@RequestMapping(value = "delete")
	public String delete(JobTask jobTask, RedirectAttributes redirectAttributes) {
		jobTaskService.delete(jobTask);
		addMessage(redirectAttributes, "删除调度任务成功");
		return "redirect:" + Global.getAdminPath() + "/tl/jobTask/?repage";
	}

	@RequiresPermissions("tl:jobTask:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			jobTaskService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}

}