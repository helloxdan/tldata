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

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.vo.ReturnWrap;
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.GroupService;
import com.thinkgem.jeesite.modules.tl.service.ScheduleService;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * 群组Controller
 * 
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/group")
public class GroupController extends BaseController {

	@Autowired
	private GroupService groupService;
	@Autowired
	private BotService botService;
	@Autowired
	private ScheduleService scheduleService;

	@RequestMapping(value = "/startSchedule")
	@ResponseBody
	public ReturnWrap startSchedule(HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			scheduleService.setRun(true);
			result.setData("OK");
		} catch (Exception e) {
			logger.error("启动调度异常，", e);
			result.fail("启动调度异常，");
		}
		return result;
	}

	@RequestMapping(value = "/stopSchedule")
	@ResponseBody
	public ReturnWrap stopSchedule(HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			scheduleService.setRun(false);
			result.setData("OK");
		} catch (Exception e) {
			logger.error("启动调度异常，", e);
			result.fail("启动调度异常，");
		}
		return result;
	}

	@ModelAttribute
	public Group get(@RequestParam(required = false) String id) {
		Group entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = groupService.get(id);
		}
		if (entity == null) {
			entity = new Group();
		}
		return entity;
	}

	@RequiresPermissions("tl:group:view")
	@RequestMapping(value = { "list", "" })
	public String list(Group group, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<Group> page = groupService.findPage(new Page<Group>(request,
				response), group);
		model.addAttribute("page", page);
		return "modules/tl/groupList";
	}

	@RequiresPermissions("tl:group:view")
	@RequestMapping(value = "form")
	public String form(Group group, Model model) {
		model.addAttribute("group", group);
		return "modules/tl/groupForm";
	}

	@RequiresPermissions("tl:group:edit")
	@RequestMapping(value = "save")
	public String save(Group group, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, group)) {
			return form(group, model);
		}

		try {
			if (StringUtils.isNotBlank(group.getUrl())) {

				// get group info by link
				// 获取目标群组信息,
				if (group.getIsNewRecord()) {
					// botService.getGroupidByUrl(group.getUrl());
					JSONObject json = botService.updateGroupInfoByLink(group
							.getUrl());
				} else {
					groupService.save(group);
				}
				addMessage(redirectAttributes, "保存群组成功");

			} else {
				addMessage(redirectAttributes, "link url 不能为空");

			}
		} catch (Exception e) {
			addMessage(redirectAttributes, e.getMessage());
			logger.error("新增或更新group失败", e);
		}
		return "redirect:" + Global.getAdminPath() + "/tl/group/?repage";
	}

	@RequiresPermissions("tl:group:edit")
	@RequestMapping(value = "delete")
	public String delete(Group group, RedirectAttributes redirectAttributes) {
		groupService.delete(group);
		addMessage(redirectAttributes, "删除群组成功");
		return "redirect:" + Global.getAdminPath() + "/tl/group/?repage";
	}

	@RequiresPermissions("tl:group:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			groupService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}

}