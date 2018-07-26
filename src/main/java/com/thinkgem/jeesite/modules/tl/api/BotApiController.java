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
import com.thinkgem.jeesite.modules.tl.entity.Group;
import com.thinkgem.jeesite.modules.tl.service.BotService;
import com.thinkgem.jeesite.modules.tl.service.GroupService;
import com.thinkgem.jeesite.modules.tl.service.RegisteService;
import com.thinkgem.jeesite.modules.tl.service.ScheduleService;
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
	private GroupService groupService;

	@Autowired
	private RegisteService registeService;
	@Autowired
	private ScheduleService scheduleService;

	/**
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/startJob")
	public ReturnWrap startJob(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String jobid = data.getJobid();
			boolean success = botService.startJob(jobid);
			if (success) {
				result.success("OK");
			} else {
				result.fail("启动失败");
			}
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("start", e);
		}
		return result;
	}

	@RequestMapping(value = "/stopJob")
	public ReturnWrap stopJob(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String jobid = data.getJobid();
			boolean success = botService.stopJob(jobid);
			result.setData(success);
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("start", e);
		}
		return result;
	}
	@RequestMapping(value = "/stopReg")
	public ReturnWrap stopReg(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			String jobid = data.getJobid();
			boolean success = botService.stopReg(jobid);
			result.setData(success);
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("start", e);
		}
		return result;
	}

	@RequestMapping(value = "/reg/addPhone")
	public ReturnWrap addRegPhone(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			registeService.addPhone(data.getPhone());
			result.setData("add phone ok");
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("init ", e);
		}
		return result;
	}

	@RequestMapping(value = "/reg/addPhoneCode")
	public ReturnWrap addRegPhoneCode(RequestData data,
			HttpServletRequest request, HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			registeService.addPhoneCode(data.getPhone(), data.getCode());
			result.setData("add phone code ok");
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("init ", e);
		}
		return result;
	}

	/**
	 * 启动账号注册。
	 * 
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/reg/auto/start")
	public ReturnWrap autoregiste(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			// registeService.autoRegiste();
			result.setData("start……");
		} catch (Exception e) {
			result.fail("启动异常，" + e.getMessage());
			logger.error("init ", e);
		}
		return result;
	}

	@RequestMapping(value = "/account/reg")
	public ReturnWrap registe(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			botService.registe(data.getPhone());
			result.setData("registe……");
		} catch (Exception e) {
			result.fail("异常，" + e.getMessage());
			logger.error("init ", e);
		}
		return result;
	}

	@RequestMapping(value = "/account/reg/setAuthCode")
	public ReturnWrap setRegCode(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			JSONObject json = botService.setRegAuthCode(data.getPhone(),
					data.getCode());
			if (json.getBooleanValue("result")) {
				result.success(json);
			} else {
				result.fail(json.getString("msg"));
				result.setData(json);
			}
		} catch (Exception e) {
			result.fail("设置注册验证码异常，" + e.getMessage());
			logger.error("setAuthCode", e);
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
	public ReturnWrap start(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
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

	/**
	 * 指定一个群组采集用户。
	 * 
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/grepUsers")
	public ReturnWrap grepUsers(RequestData data, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		try {
			Group g = new Group();
			g = groupService.get(data.getChatId() + "");
			scheduleService.fetchUserFromGroup(data.getPhone(), g);
			result.success("OK");
		} catch (Exception e) {
			result.fail("采集用户信息异常，" + e.getMessage());
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
