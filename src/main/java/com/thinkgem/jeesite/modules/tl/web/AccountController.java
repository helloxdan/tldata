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
import com.thinkgem.jeesite.modules.api.vo.ReturnWrap;
import com.thinkgem.jeesite.modules.tl.entity.Account;
import com.thinkgem.jeesite.modules.tl.service.AccountService;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;
/**
 * 登录账号Controller
 * @author admin
 * @version 2018-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/tl/account")
public class AccountController extends BaseController {

	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value = "/addBatch")
	public ReturnWrap addBatch(RequestData data,
			HttpServletRequest request, HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String status = accountService.addBatch(data);
			result.setData(status);
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
		}
		return result;
	}
	@ModelAttribute
	public Account get(@RequestParam(required=false) String id) {
		Account entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = accountService.get(id);
		}
		if (entity == null){
			entity = new Account();
		}
		return entity;
	}
	
	@RequiresPermissions("tl:account:view")
	@RequestMapping(value = {"list", ""})
	public String list(Account account, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Account> page = accountService.findPage(new Page<Account>(request, response), account); 
		model.addAttribute("page", page);
		return "modules/tl/accountList";
	}

	@RequiresPermissions("tl:account:view")
	@RequestMapping(value = "form")
	public String form(Account account, Model model) {
		model.addAttribute("account", account);
		return "modules/tl/accountForm";
	}

	@RequiresPermissions("tl:account:edit")
	@RequestMapping(value = "save")
	public String save(Account account, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, account)){
			return form(account, model);
		}
		accountService.save(account);
		addMessage(redirectAttributes, "保存登录账号成功");
		return "redirect:"+Global.getAdminPath()+"/tl/account/?repage";
	}
	
	@RequiresPermissions("tl:account:edit")
	@RequestMapping(value = "delete")
	public String delete(Account account, RedirectAttributes redirectAttributes) {
		accountService.delete(account);
		addMessage(redirectAttributes, "删除登录账号成功");
		return "redirect:"+Global.getAdminPath()+"/tl/account/?repage";
	}
	
	@RequiresPermissions("tl:account:edit")
	@RequestMapping(value = "del")
	@ResponseBody
	public ModelMap del(String ids, RedirectAttributes redirectAttributes) {
		ModelMap modelMap = new ModelMap();
		try {
			accountService.del(ids);
			modelMap.put("success", true);
		} catch (Exception e) {
			logger.error("删除记录失败", e);
			modelMap.put("success", false);
			modelMap.put("msg", "删除记录失败");
		}
		return modelMap;
	}
	

}