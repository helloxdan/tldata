/**
 * 
 */
package com.thinkgem.jeesite.modules.tl.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.bot.structure.LoginStatus;

import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.vo.ReturnWrap;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.RegisteService;
import com.thinkgem.jeesite.modules.tl.vo.RequestData;

/**
 * API入口。
 * 
 * @author ThinkPad
 *
 */
@RestController
@RequestMapping(value = "/api/tl")
public class BotApiController extends BaseController {

	@Autowired
	private BotService botService;
	@Autowired
	private RegisteService registeService;

	/**
	 * 启动账号注册。
	 * 
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/account/reg/start")
	public ReturnWrap registe(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			registeService.autoRegiste();
			result.setData("start……");
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("init ", e);
		}
		return result;
	}

	@RequestMapping(value = "/account/init")
	public ReturnWrap accountInit(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String status = botService.accountInit(data);
			result.setData(status);
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("init ", e);
		}
		return result;
	}

	/**
	 * 批量启动
	 * 
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/startBatch")
	public ReturnWrap startBatch(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String status = botService.startBatch(data);
			result.setData(status);
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("startBatch", e);
		}
		return result;
	}

	@RequestMapping(value = "/start")
	public ReturnWrap sendImMessage(RequestData data,
			HttpServletRequest request, HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			LoginStatus status = botService.start(data);
			result.setData(status);
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("start", e);
		}
		return result;
	}

	@RequestMapping(value = "/getState")
	public ReturnWrap getState(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			JSONObject state = botService.getState(data);
			result.success(state);

		} catch (Exception e) {
			result.fail("取状态异常，" + e.getMessage());
			logger.error("getState", e);
		}
		return result;
	}

	@RequestMapping(value = "/setAuthCode")
	public ReturnWrap setCode(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			boolean success = botService.setAuthCode(data);
			if (success) {
				result.success("OK");
			} else {
				result.fail("验证失败");
			}
		} catch (Exception e) {
			result.fail("设置验证码异常，" + e.getMessage());
			logger.error("setAuthCode", e);
		}
		return result;
	}

	@RequestMapping(value = "/setAdmin")
	public ReturnWrap setAdmin(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			boolean success = botService.setAdmin(data);
			if (success) {
				result.success("OK");
			} else {
				result.fail("ERROR");
			}
		} catch (Exception e) {
			result.fail("设置管理员异常，" + e.getMessage());
			logger.error("setAdmin", e);
		}
		return result;
	}

	@RequestMapping(value = "/groupInfo")
	public ReturnWrap groupInfo(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			JSONObject json = botService.groupInfo(data);
			if (json.containsKey("title")) {
				result.success(json);
			} else {
				result.fail("ERROR");
			}
		} catch (Exception e) {
			result.fail("取群详情，" + e.getMessage());
			logger.error("groupInfo", e);
		}
		return result;
	}

	@RequestMapping(value = "/importInvite")
	public ReturnWrap importInvite(RequestData data,
			HttpServletRequest request, HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			JSONObject json = botService.importInvite(data);
			if (json.getBooleanValue("success")) {
				result.success(json);
			} else {
				result.fail("ERROR");
			}
		} catch (Exception e) {
			result.fail("加群异常，" + e.getMessage());
			logger.error("importInvite ", e);
		}
		return result;
	}

	@RequestMapping(value = "/collectUsers")
	public ReturnWrap collectUsers(RequestData data,
			HttpServletRequest request, HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			botService.collectUsers(data);
			result.success("OK");
		} catch (Exception e) {
			result.fail("收集用户信息异常，" + e.getMessage());
			logger.error("collectUsers", e);
		}

		return result;
	}

	@RequestMapping(value = "/cleanJobUser")
	public ReturnWrap cleanJobUser(RequestData data,
			HttpServletRequest request, HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			botService.cleanJobUser(data);
			result.success("OK");
		} catch (Exception e) {
			result.fail("清洗用户信息异常，" + e.getMessage());
			logger.error("cleanJobUser", e);
		}

		return result;
	}

	@RequestMapping(value = "/addUsers")
	public ReturnWrap addUsers(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			botService.addUsers(data);
			result.success("OK");
		} catch (Exception e) {
			result.fail("收集用户信息异常，" + e.getMessage());
			logger.error("addUsers", e);
		}

		return result;
	}

	@RequestMapping(value = "/stop")
	public ReturnWrap stop(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			botService.stop(data);
			result.success("OK");
		} catch (Exception e) {
			result.fail("停止异常，" + e.getMessage());
			logger.error("stop", e);
		}

		return result;
	}

}
