package org.telegram.plugins.xuser.entity;

import org.telegram.api.user.profile.photo.TLAbsUserProfilePhoto;
import org.telegram.api.user.status.TLAbsUserStatus;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.XUtils;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class User implements IUser {
	private final int userId; // /< ID of the user (provided by Telegram server)
	private Long userHash; // /< Hash of the user (provide by Telegram server)
	private String username;
	private String account;
	private String firstName = "";
	private String lastName = "";

	private String langCode;

	public User(int uid) {
		this.userId = uid;
	}

	public User(User copy) {
		this.userId = copy.getUserId();
		this.userHash = copy.getUserHash();
	}

	@Override
	public int getUserId() {
		return this.userId;
	}

	@Override
	public Long getUserHash() {
		return userHash;
	}

	public void setUserHash(Long userHash) {
		this.userHash = userHash;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "" + this.userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = XUtils.transChartset(firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = XUtils.transChartset(lastName);
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
}
