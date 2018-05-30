package org.telegram.plugins.xuser.db;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief TODO
 * @date 16 of October of 2016
 */
class DatabaseConstants {
//    static final String controllerDB = "com.mysql.cj.jdbc.Driver";
    static final String controllerDB = "com.mysql.jdbc.Driver";
    static final String userDB = "telegram";
    private static final String databaseName = "telegram";
    static final String password = "tl123456";
    static final String linkDB = "jdbc:mysql://localhost:3306/" + databaseName + "?useUnicode=true&characterEncoding=utf-8";
}
