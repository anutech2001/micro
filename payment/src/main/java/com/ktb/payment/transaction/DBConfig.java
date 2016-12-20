package com.ktb.payment.transaction;

public class DBConfig {
	private static String dbHost;
	private static String dbPort;
	private static String dbName;
	private static String dbUser;
	private static String dbPassword;
	
	public DBConfig(String host, String name, String port, String user, String password) {
		dbHost = host;
		dbName = name;
		dbPort = port;
		dbUser = user;
		dbPassword = password;
	}

	public static String getDbHost() {
		return dbHost;
	}

	public static String getDbPort() {
		return dbPort;
	}

	public static String getDbUser() {
		return dbUser;
	}

	public static String getDbPassword() {
		return dbPassword;
	}

	public static String getDbName() {
		return dbName;
	}

}
