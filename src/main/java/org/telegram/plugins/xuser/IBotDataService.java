package org.telegram.plugins.xuser;

import java.util.List;

import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.BotConfig;
import org.telegram.plugins.xuser.entity.ChatImpl;
import org.telegram.plugins.xuser.entity.User;

public interface IBotDataService extends DatabaseManager{

	void setBotConfig(BotConfig botConfig);

	boolean addUser(User user);

	boolean updateUser(User user);

	boolean updateChat(ChatImpl current);

	boolean addChat(ChatImpl current);

	List<User> findUsers();
	 
}
