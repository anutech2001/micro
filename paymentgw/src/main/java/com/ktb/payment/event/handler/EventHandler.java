package com.ktb.payment.event.handler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.transaction.TransactionMgnt;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class EventHandler {
	private final static Logger logger = LoggerFactory.getLogger(EventHandler.class);
	private final static String QUEUE_NAME = "paymentTo3rdCreated";


	public static void handlePaymentTo3rdCreatedEvent() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(EventConfig.getHost());
		
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			logger.info(" [*] Waiting for messages. To exit press CTRL+C");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					// change trxStatus = completed(COMP)
					logger.info(" [x] Received '" + message + "'");
					TransactionMgnt.updatePaymentTransaction(message);
				}
			};
			
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
