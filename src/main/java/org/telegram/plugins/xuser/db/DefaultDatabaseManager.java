package org.telegram.plugins.xuser.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;
import org.telegram.plugins.xuser.entity.ChatImpl;
import org.telegram.plugins.xuser.entity.User;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
public class DefaultDatabaseManager implements DatabaseManager {
	private static final String LOGTAG = "DATABASEMANAGER";
	private static volatile ConnectionDB connetion;
	BotConfig botConfig = null;

	/**
	 * Private constructor (due to Singleton)
	 */
	public DefaultDatabaseManager() {
		connetion = new ConnectionDB();
		final int currentVersion = connetion.checkVersion();
		BotLogger.info(LOGTAG, "Current db version: " + currentVersion);
		if (currentVersion < CreationStrings.version) {
			recreateTable(currentVersion);
		}
	}

	private String getPhone() {
		return getBotConfig().getPhoneNumber();
	}

	public void begin() {
		try {
			connetion.initTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commit() {
		try {
			connetion.commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recreates the DB
	 */
	private void recreateTable(int currentVersion) {
		try {
			connetion.initTransaction();
			if (currentVersion == 0) {
				createNewTables();
			}
			connetion.commitTransaction();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	private int createNewTables() throws SQLException {
		connetion.executeQuery(CreationStrings.createVersionTable);
		connetion.executeQuery(CreationStrings.createUsersTable);
		connetion.executeQuery(CreationStrings.insertCurrentVersion);
		connetion.executeQuery(CreationStrings.createChatTable);
		connetion.executeQuery(CreationStrings.createDifferencesDataTable);
		return CreationStrings.version;
	}

	/**
	 * Gets an user by id
	 *
	 * @param userId
	 *            ID of the user
	 * @return User requested or null if it doesn't exists
	 * @see User
	 */
	@Override
	public @Nullable IUser getUserById(int userId) {
		User user = null;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("SELECT * FROM tl_Users WHERE userId= ? and account=?");
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, getPhone());
			final ResultSet result = preparedStatement.executeQuery();
			if (result.next()) {
				user = new User(userId);
				user.setUserHash(result.getLong("userHash"));
			}
			result.close();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return user;
	}

	public @Nullable List<User> findUsers() {
		User user = null;
		List<User> users = new ArrayList<User>();
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("SELECT * FROM tl_Users order by update_date desc");

			final ResultSet result = preparedStatement.executeQuery();

			while (result.next()) {
				user = new User(result.getInt("userId"));
				user.setUserHash(result.getLong("userHash"));
				user.setAccount(result.getString("account"));
				user.setUsername(result.getString("username"));
				users.add(user);
			}
			result.close();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return users;
	}

	/**
	 * Adds an user to the database
	 *
	 * @param user
	 *            User to be added
	 * @return true if it was added, false otherwise
	 * @see User
	 */
	public boolean addUser(@NotNull User user) {
		int updatedRows = 0;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("INSERT INTO tl_Users (id,account,userId, userHash,username,update_date) "
							+ "VALUES (?,?,?,?)");
			preparedStatement.setString(1, getPhone() + user.getUserId());
			preparedStatement.setString(2, getPhone());
			preparedStatement.setInt(3, user.getUserId());
			if ((user.getUserHash() == null) || (user.getUserHash() == 0L)) {
				preparedStatement.setNull(4, Types.NUMERIC);
			} else {
				preparedStatement.setLong(4, user.getUserHash());
			}
			preparedStatement.setString(5, user.getUsername());
			preparedStatement.setTimestamp(6,
					new Timestamp(System.currentTimeMillis()));
			updatedRows = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return updatedRows > 0;
	}

	public boolean updateUser(@NotNull User user) {
		int updatedRows = 0;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("UPDATE tl_Users SET userHash=? ,username=?,update_date=?"
							+ "WHERE userId=? and account=?");
			if ((user.getUserHash() == null) || (user.getUserHash() == 0L)) {
				preparedStatement.setNull(1, Types.NUMERIC);
			} else {
				preparedStatement.setLong(1, user.getUserHash());
			}
			preparedStatement.setString(2, user.getUsername());
			preparedStatement.setTimestamp(3,
					new Timestamp(System.currentTimeMillis()));
			preparedStatement.setInt(4, user.getUserId());
			preparedStatement.setString(5, getPhone());
			updatedRows = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return updatedRows > 0;
	}

	@Override
	public @Nullable Chat getChatById(int chatId) {
		ChatImpl channel = null;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("SELECT * FROM tl_Chat WHERE chatid= ? and account=?");
			preparedStatement.setInt(1, chatId);
			preparedStatement.setString(2, getPhone());
			final ResultSet result = preparedStatement.executeQuery();
			if (result.next()) {
				channel = new ChatImpl(chatId);
				channel.setAccessHash(result.getLong("accessHash"));
				channel.setChannel(result.getBoolean("isChannel"));
			}
			result.close();
		} catch (SQLException e) {
			BotLogger.severe(LOGTAG, e);
		}

		return channel;
	}

	/**
	 * Adds a chat to the database
	 *
	 * @param chat
	 *            User to be added
	 * @return true if it was added, false otherwise
	 * @see User
	 */
	public boolean addChat(@NotNull ChatImpl chat) {
		int updatedRows = 0;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("INSERT INTO tl_Chat (id,account,chatid, accessHash, isChannel,title,update_date) "
							+ "VALUES (?,?,?,?)");
			preparedStatement.setString(1, getPhone() + chat.getId());
			preparedStatement.setString(2, getPhone());
			preparedStatement.setInt(3, chat.getId());
			if (chat.getAccessHash() == null) {
				preparedStatement.setNull(4, Types.BIGINT);
			} else {
				preparedStatement.setLong(4, chat.getAccessHash());
			}
			preparedStatement.setBoolean(5, chat.isChannel());
			preparedStatement.setString(6, chat.getTitle());
			preparedStatement.setTimestamp(7,
					new Timestamp(System.currentTimeMillis()));
			updatedRows = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return updatedRows > 0;
	}

	public boolean updateChat(ChatImpl chat) {
		int updatedRows = 0;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("UPDATE tl_Chat SET accessHash=?, isChannel=? "
							+ "WHERE chatid=? and account=?");
			preparedStatement.setLong(1, chat.getAccessHash());
			preparedStatement.setBoolean(2, chat.isChannel());
			preparedStatement.setInt(3, chat.getId());
			preparedStatement.setString(4, getPhone());
			updatedRows = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return updatedRows > 0;
	}

	@Override
	public @NotNull HashMap<Integer, int[]> getDifferencesData() {
		final HashMap<Integer, int[]> differencesDatas = new HashMap<>();
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("SELECT * FROM tl_DifferencesData");
			final ResultSet result = preparedStatement.executeQuery();
			while (result.next()) {
				final int[] differencesData = new int[3];
				differencesData[0] = result.getInt("pts");
				differencesData[1] = result.getInt("date");
				differencesData[2] = result.getInt("seq");
				differencesDatas.put(result.getInt("botId"), differencesData);
			}
			result.close();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return differencesDatas;
	}

	@Override
	public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
		int updatedRows = 0;
		try {
			final PreparedStatement preparedStatement = connetion
					.getPreparedStatement("REPLACE INTO tl_DifferencesData (botId, pts, date, seq) VALUES (?, ?, ?, ?);");
			preparedStatement.setInt(1, botId);
			preparedStatement.setInt(2, pts);
			preparedStatement.setInt(3, date);
			preparedStatement.setInt(4, seq);
			updatedRows = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			BotLogger.error(LOGTAG, e);
		}
		return updatedRows > 0;
	}

	@Override
	protected void finalize() throws Throwable {
		connetion.closeConexion();
		super.finalize();
	}

	public BotConfig getBotConfig() {
		return botConfig;
	}

	public void setBotConfig(BotConfig botConfig) {
		this.botConfig = botConfig;
	}

}
