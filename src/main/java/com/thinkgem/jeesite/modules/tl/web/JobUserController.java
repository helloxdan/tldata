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
import com.thinkgem.jeesite.modules.tl.entity.JobUser;
import com.thinkgem.jeesite.modules.tl.service.JobUserService;
/**
 * 邀请用户Controller
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/jobUser")
public class JobUserController extends BaseController {

	@Autowired
	private JobUserService jobUserService;
	
	@ModelAttribute
	public JobUser get(@RequestParam(required=false) String id) {
		JobUser entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jobUserService.get(id);
		}
		if (entity == null){
			entity = new JobUser();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:jobUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(JobUser jobUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JobUser> page = jobUserService.findPage(new Page<JobUser>(request, response), jobUser); 
		model.addAttribute("page", page);
		return "modules/tl/jobUserList";
	}

	@RequiresPermissions("tl:jobUser:view")
	@RequestMapping(value = "form")
	public String form(JobUser jobUser, Model model) {
		model.addAttribute("jobUser", jobUser);
		return "modules/tl/jobUserForm";
	}

	@RequiresPermissions("tl:jobUser:edit")
	@RequestMapping(value = "save")
	public String save(JobUser jobUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jobUser)){
			return form(jobUser, model);
		}
		jobUserService.save(jobUser);
		addMessage(redirectAttributes, "保存邀请用户成功");
		return "redirect:"+Global.getAdminPath()+"/tl/jobUser/?repage";
	}
	
	@RequiresPermissions("tl:jobUser:edit")
	@RequestMapping(value = "delete")
	public String delete(JobUser jobUser, RedirectAttributes redirectAttributes) {
		jobUserService.delete(jobUser);
		addMessage(redirectAttributes, "删除邀请用户成功");
		return "redirect:"+Global.getAdminPath()+"/tl/jobUser/?repage";
	}
	
	@RequiresPermissions("tl:jobUser:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			jobUserService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}