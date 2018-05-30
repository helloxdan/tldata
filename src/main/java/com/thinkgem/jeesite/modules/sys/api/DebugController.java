/**
 * 
 */
package com.thinkgem.jeesite.modules.sys.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.api.vo.DebugParam;
import com.thinkgem.jeesite.modules.api.vo.ReturnWrap;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.plugin.umeng.AndroidNotification;
import com.thinkgem.jeesite.plugin.umeng.DisplayType;
import com.thinkgem.jeesite.plugin.umeng.PushHelper;

/**
 * 调试API入口。
 * 
 * @author ThinkPad
 *
 */
@RestController
@RequestMapping(value = "/api/debug")
public class DebugController extends BaseController {

	@Autowired
	private SystemService systemService;

	@RequestMapping(value = "/push/mode")
	public ReturnWrap sendImMessage(@RequestParam(name = "mode", required = false) String mode, HttpServletRequest request,
			HttpServletResponse response) {
		ReturnWrap result = new ReturnWrap(true);
		PushHelper.getInstance().setMode(mode);
		return result;
	}

	/**
	 * 消息推送测试。
	 * 
	 * @param request
	 * @param response
	 * @param city
	 * @return
	 */
	@RequestMapping(value = "/push")
	public ReturnWrap push(@ModelAttribute("param") DebugParam param, HttpServletRequest request,
			HttpServletResponse response) {
		String mobile = param.getMobile();
		if (StringUtils.isBlank(mobile)) {
			return new ReturnWrap(false, "手机号为空");
		}
		ReturnWrap result = new ReturnWrap(true);
		// LiveHost host = liveHostService.findHostByMobile(mobile);
		User user = systemService.getUserByLoginName(mobile);
		if (user == null) {
			return new ReturnWrap(false, "用户不存在，无法推送");
		}
		Map<String, String> map = new HashMap<String, String>();

		Map paramMap = request.getParameterMap();
		Set ks = paramMap.keySet();
		for (Object k : ks) {
			System.out.println(k + "-" + paramMap.get(k));
			String paramName = (String) k;
			// key-开头的，是自定义消息体
			if (paramName.startsWith("key-")) {
				map.put(paramName.substring(4),
						(String) request.getParameter(paramName));
			}
		}

		// try {
		// String json = param.getJson();
		// JSONObject obj = JSON.parseObject(json);
		// Set<String> keys = obj.keySet();
		// for (String key : keys) {
		// map.put(key, obj.getString(key));
		// }
		// } catch (Exception e) {
		// result = new ReturnWrap(false, "json字符串格式解析错误");
		// }

		String errorMsg = null;
		try {
			// 推送
			if ("android".equals(param.getPlatform())) {
				DisplayType displayType =  DisplayType.NOTIFICATION;

				// 消息
				if ("message".equals(displayType))
					displayType =  DisplayType.MESSAGE;

				errorMsg = PushHelper.getInstance()
						.sendAndroidCustomizedcastMessage(user.getId(),
								param.getTitle(), param.getMsg(), null,
								displayType, map);
			} else {
				errorMsg = PushHelper.getInstance().sendIOSCustomizedcast(
						user.getId(), param.getTitle(), param.getMsg(),
						param.getBell(), map);
			}
			if (errorMsg != null)
				result = new ReturnWrap(false, errorMsg);
		} catch (Exception e) {
			result = new ReturnWrap(false, "推送消息失败，" + e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/recorderror")
	public ReturnWrap recordError(@RequestParam(name="errortype",required=false)  String errortype, @RequestBody String error) {
		ReturnWrap result = new ReturnWrap(true);
		if (errortype == null || StringUtils.isBlank(errortype)
				|| error == null || StringUtils.isBlank(error)) {
			result.fail("必须指定错误类型和提供错误信息！");
		}
		if (errortype.length() > 10) {
			errortype = errortype.substring(0, 10);
		}

		logger.error("client log:{},{}", errortype, error);
		result.setData("OK");
		return result;
	}

	 

}
