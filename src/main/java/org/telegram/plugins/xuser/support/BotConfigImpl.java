package org.telegram.plugins.xuser.support;

import java.io.File;

import org.telegram.bot.structure.BotConfig;

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
		File auth = new File("auth");
		if (!auth.exists())
			auth.mkdir();

		setAuthfile("auth/"+phoneNumber + ".auth");
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
