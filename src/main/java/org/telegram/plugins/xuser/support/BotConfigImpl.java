package org.telegram.plugins.xuser.support;

import java.io.File;

import org.telegram.bot.structure.BotConfig;

import com.thinkgem.jeesite.common.config.Global;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class BotConfigImpl extends BotConfig {
	private String phoneNumber;

	public BotConfigImpl(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		// 按目录存放，auth/
		String path=Global.getConfig("tl.auth.path");
		File auth = new File(path);
		if (!auth.exists())
			auth.mkdir();

		setAuthfile(auth.getPath() + File.separator + phoneNumber + ".auth");
	}

	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String getBotToken() {
		return null;
	}

	@Override
	public boolean isBot() {
		return false;
	}
}
