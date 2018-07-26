/**
 * Copyright &copy; 2017-2020 <a href="https://www.gzruimin.com">gzruimin</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.tl.web;
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
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.tl.entity.JobGroup;
import com.thinkgem.jeesite.modules.tl.service.JobGroupService;
/**
 * 任务采集群组列表Controller
 * @author admin
 * @version 2018-07-25
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/jobGroup")
public class JobGroupController extends BaseController {

	@Autowired
	private JobGroupService jobGroupService;
	
	@ModelAttribute
	public JobGroup get(@RequestParam(required=false) String id) {
		JobGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jobGroupService.get(id);
		}
		if (entity == null){
			entity = new JobGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:jobGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(JobGroup jobGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JobGroup> page = jobGroupService.findPage(new Page<JobGroup>(request, response), jobGroup); 
		model.addAttribute("page", page);
		return "modules/tl/jobGroupList";
	}

	@RequiresPermissions("tl:jobGroup:view")
	@RequestMapping(value = "form")
	public String form(JobGroup jobGroup, Model model) {
		model.addAttribute("jobGroup", jobGroup);
		return "modules/tl/jobGroupForm";
	}

	@RequiresPermissions("tl:jobGroup:edit")
	@RequestMapping(value = "save")
	public String save(JobGroup jobGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jobGroup)){
			return form(jobGroup, model);
		}
		jobGroupService.save(jobGroup);
		addMessage(redirectAttributes, "保存任务采集群组列表成功");
		return "redirect:"+Global.getAdminPath()+"/tl/jobGroup/?repage";
	}
	
	@RequiresPermissions("tl:jobGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(JobGroup jobGroup, RedirectAttributes redirectAttributes) {
		jobGroupService.delete(jobGroup);
		addMessage(redirectAttributes, "删除任务采集群组列表成功");
		return "redirect:"+Global.getAdminPath()+"/tl/jobGroup/?repage";
	}
	
	@RequiresPermissions("tl:jobGroup:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			jobGroupService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}