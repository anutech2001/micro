package com.ktb.payment.event;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EventHandler {
	private final static Logger logger = LoggerFactory.getLogger(EventHandler.class);
	private final static String QUEUE_NAME = "paymentTo3rdCreated";


	public static void paymentTo3rdCreatedEvent(String msg) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(EventConfig.getHost());
		factory.setPort(Integer.valueOf(EventConfig.getPort()));
		factory.setUsername(EventConfig.getUser());
		factory.setPassword(EventConfig.getPassword());
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
			logger.info(" [x] Sent '" + msg + "'");
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

}
