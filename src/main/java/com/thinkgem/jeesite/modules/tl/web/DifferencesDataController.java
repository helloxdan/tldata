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
import com.thinkgem.jeesite.modules.tl.entity.DifferencesData;
import com.thinkgem.jeesite.modules.tl.service.DifferencesDataService;
/**
 * 差异数据Controller
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/differencesData")
public class DifferencesDataController extends BaseController {

	@Autowired
	private DifferencesDataService differencesDataService;
	
	@ModelAttribute
	public DifferencesData get(@RequestParam(required=false) String id) {
		DifferencesData entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = differencesDataService.get(id);
		}
		if (entity == null){
			entity = new DifferencesData();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:differencesData:view")
	@RequestMapping(value = {"list", ""})
	public String list(DifferencesData differencesData, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DifferencesData> page = differencesDataService.findPage(new Page<DifferencesData>(request, response), differencesData); 
		model.addAttribute("page", page);
		return "modules/tl/differencesDataList";
	}

	@RequiresPermissions("tl:differencesData:view")
	@RequestMapping(value = "form")
	public String form(DifferencesData differencesData, Model model) {
		model.addAttribute("differencesData", differencesData);
		return "modules/tl/differencesDataForm";
	}

	@RequiresPermissions("tl:differencesData:edit")
	@RequestMapping(value = "save")
	public String save(DifferencesData differencesData, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, differencesData)){
			return form(differencesData, model);
		}
		differencesDataService.save(differencesData);
		addMessage(redirectAttributes, "保存差异数据成功");
		return "redirect:"+Global.getAdminPath()+"/tl/differencesData/?repage";
	}
	
	@RequiresPermissions("tl:differencesData:edit")
	@RequestMapping(value = "delete")
	public String delete(DifferencesData differencesData, RedirectAttributes redirectAttributes) {
		differencesDataService.delete(differencesData);
		addMessage(redirectAttributes, "删除差异数据成功");
		return "redirect:"+Global.getAdminPath()+"/tl/differencesData/?repage";
	}
	
	@RequiresPermissions("tl:differencesData:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			differencesDataService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}