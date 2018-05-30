package org.telegram.plugins.xuser.db;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
class CreationStrings {
    static final int version = 1;
    static final String createVersionTable = "CREATE TABLE IF NOT EXISTS Versions(ID INTEGER PRIMARY KEY AUTO_INCREMENT, Version INTEGER);";
    static final String insertCurrentVersion = "INSERT IGNORE INTO Versions (Version) VALUES(" + version + ");";
    static final String createUsersTable = "CREATE TABLE IF NOT EXISTS Users(" +
            "id varchar(100) NOT NULL,account varchar(15) NOT NULL,userId INTEGER NOT NULL, " +
            "userHash BIGINT DEFAULT NULL, username varchar(100) NOT NULL,update_date DATETIME," +
            "CONSTRAINT `userPrimaryKey` PRIMARY KEY(id));";
    static final String createDifferencesDataTable = "create table if not exists DifferencesData (" +
            "botId INTEGER PRIMARY KEY NOT NULL, " +
            "pts INTEGER NOT NULL, " +
            "date INTEGER NOT NULL, " +
            "seq INTEGER NOT NULL);";
    static final String createChatTable = "CREATE TABLE IF NOT EXISTS Chat (" +
            "id varchar(100) PRIMARY KEY NOT NULL,account varchar(15) NOT NULL,chatid INTEGER  NOT NULL," +
            "isChannel BOOLEAN NOT NULL DEFAULT FALSE, " +
            "title VARCHAR(100) , " +
            "accessHash BIGINT DEFAULT NULL,update_date DATETIME" +
            ");";
}
