package com.ktb.payment.event.handler;

public class EventConfig {
	private static String host;
	private static String port;
	private static String user;
	private static String password;
	
	public EventConfig(String host, String port, String user, String password) {
		EventConfig.host = host;
		EventConfig.port = port;
		EventConfig.user = user;
		EventConfig.password = password;
	}
	public static String getHost() {
		return host;
	}
	public static String getPort() {
		return port;
	}
	public static String getUser() {
		return user;
	}
	public static String getPassword() {
		return password;
	}
}
