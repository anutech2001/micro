package com.ktb.payment.event.handler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.model.PaymentTransaction;
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
	private final static String PAYMENT_CREATED_QUEUE_NAME = "paymentTo3rdCreated";
	private final static String PAYMENT_COMPLETED_QUEUE_NAME = "paymentTo3rdCompleted";


	public static void handlePaymentTo3rdCreatedEvent() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(EventConfig.getHost());
		
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(PAYMENT_CREATED_QUEUE_NAME, false, false, false, null);
			logger.info(" [*] Waiting for messages. To exit press CTRL+C");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					// change trxStatus = completed(COMP)
					logger.info(" [x] Received '" + message + "'");
					PaymentTransaction trx = TransactionMgnt.updatePaymentTransaction(message);
					if (trx != null) {
						paymentTo3rdCompletedEvent(TransactionMgnt.encodePaymentTransacion(trx));
					}
				}
			};
			
			channel.basicConsume(PAYMENT_CREATED_QUEUE_NAME, true, consumer);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	public static void paymentTo3rdCompletedEvent(String msg) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(EventConfig.getHost());
		factory.setPort(Integer.valueOf(EventConfig.getPort()));
		factory.setUsername(EventConfig.getUser());
		factory.setPassword(EventConfig.getPassword());
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(PAYMENT_COMPLETED_QUEUE_NAME, false, false, false, null);
			channel.basicPublish("", PAYMENT_COMPLETED_QUEUE_NAME, null, msg.getBytes());
			logger.info(" [x] Sent '" + msg + "'");
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
