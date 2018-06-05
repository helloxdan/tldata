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
import com.thinkgem.jeesite.modules.tl.entity.Chat;
import com.thinkgem.jeesite.modules.tl.service.ChatService;
/**
 * 群组会话记录Controller
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/chat")
public class ChatController extends BaseController {

	@Autowired
	private ChatService chatService;
	
	@ModelAttribute
	public Chat get(@RequestParam(required=false) String id) {
		Chat entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chatService.get(id);
		}
		if (entity == null){
			entity = new Chat();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:chat:view")
	@RequestMapping(value = {"list", ""})
	public String list(Chat chat, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Chat> page = chatService.findPage(new Page<Chat>(request, response), chat); 
		model.addAttribute("page", page);
		return "modules/tl/chatList";
	}

	@RequiresPermissions("tl:chat:view")
	@RequestMapping(value = "form")
	public String form(Chat chat, Model model) {
		model.addAttribute("chat", chat);
		return "modules/tl/chatForm";
	}

	@RequiresPermissions("tl:chat:edit")
	@RequestMapping(value = "save")
	public String save(Chat chat, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, chat)){
			return form(chat, model);
		}
		chatService.save(chat);
		addMessage(redirectAttributes, "保存群组会话记录成功");
		return "redirect:"+Global.getAdminPath()+"/tl/chat/?repage";
	}
	
	@RequiresPermissions("tl:chat:edit")
	@RequestMapping(value = "delete")
	public String delete(Chat chat, RedirectAttributes redirectAttributes) {
		chatService.delete(chat);
		addMessage(redirectAttributes, "删除群组会话记录成功");
		return "redirect:"+Global.getAdminPath()+"/tl/chat/?repage";
	}
	
	@RequiresPermissions("tl:chat:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			chatService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}