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
import com.thinkgem.jeesite.modules.tl.entity.TlUser;
import com.thinkgem.jeesite.modules.tl.service.TlUserService;
/**
 * 好友用户Controller
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/tlUser")
public class TlUserController extends BaseController {

	@Autowired
	private TlUserService tlUserService;
	
	@ModelAttribute
	public TlUser get(@RequestParam(required=false) String id) {
		TlUser entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tlUserService.get(id);
		}
		if (entity == null){
			entity = new TlUser();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:tlUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(TlUser tlUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TlUser> page = tlUserService.findPage(new Page<TlUser>(request, response), tlUser); 
		model.addAttribute("page", page);
		return "modules/tl/tlUserList";
	}

	@RequiresPermissions("tl:tlUser:view")
	@RequestMapping(value = "form")
	public String form(TlUser tlUser, Model model) {
		model.addAttribute("tlUser", tlUser);
		return "modules/tl/tlUserForm";
	}

	@RequiresPermissions("tl:tlUser:edit")
	@RequestMapping(value = "save")
	public String save(TlUser tlUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, tlUser)){
			return form(tlUser, model);
		}
		tlUserService.save(tlUser);
		addMessage(redirectAttributes, "保存好友用户成功");
		return "redirect:"+Global.getAdminPath()+"/tl/tlUser/?repage";
	}
	
	@RequiresPermissions("tl:tlUser:edit")
	@RequestMapping(value = "delete")
	public String delete(TlUser tlUser, RedirectAttributes redirectAttributes) {
		tlUserService.delete(tlUser);
		addMessage(redirectAttributes, "删除好友用户成功");
		return "redirect:"+Global.getAdminPath()+"/tl/tlUser/?repage";
	}
	
	@RequiresPermissions("tl:tlUser:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			tlUserService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}