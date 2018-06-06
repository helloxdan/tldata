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
import com.thinkgem.jeesite.modules.tl.entity.UserSession;
import com.thinkgem.jeesite.modules.tl.service.UserSessionService;
/**
 * 用户会话Controller
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/userSession")
public class UserSessionController extends BaseController {

	@Autowired
	private UserSessionService userSessionService;
	
	@ModelAttribute
	public UserSession get(@RequestParam(required=false) String id) {
		UserSession entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userSessionService.get(id);
		}
		if (entity == null){
			entity = new UserSession();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:userSession:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserSession userSession, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<UserSession> page = userSessionService.findPage(new Page<UserSession>(request, response), userSession); 
		model.addAttribute("page", page);
		return "modules/tl/userSessionList";
	}

	@RequiresPermissions("tl:userSession:view")
	@RequestMapping(value = "form")
	public String form(UserSession userSession, Model model) {
		model.addAttribute("userSession", userSession);
		return "modules/tl/userSessionForm";
	}

	@RequiresPermissions("tl:userSession:edit")
	@RequestMapping(value = "save")
	public String save(UserSession userSession, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, userSession)){
			return form(userSession, model);
		}
		userSessionService.save(userSession);
		addMessage(redirectAttributes, "保存用户会话成功");
		return "redirect:"+Global.getAdminPath()+"/tl/userSession/?repage";
	}
	
	@RequiresPermissions("tl:userSession:edit")
	@RequestMapping(value = "delete")
	public String delete(UserSession userSession, RedirectAttributes redirectAttributes) {
		userSessionService.delete(userSession);
		addMessage(redirectAttributes, "删除用户会话成功");
		return "redirect:"+Global.getAdminPath()+"/tl/userSession/?repage";
	}
	
	@RequiresPermissions("tl:userSession:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			userSessionService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}