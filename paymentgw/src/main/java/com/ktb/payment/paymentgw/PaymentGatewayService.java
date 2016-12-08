package com.ktb.payment.paymentgw;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
public class PaymentGatewayService {
	private final static Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);
	private final static String QUEUE_NAME = "paymentTo3rdCreated";
	private final static String PAYMENTGATEWAY = "";


	  public static void main(String[] argv)
	      throws java.io.IOException,
	             java.lang.InterruptedException, TimeoutException {

	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost(System.getenv("RabbitHost"));
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    logger.info(PAYMENTGATEWAY + " [*] Waiting for messages. To exit press CTRL+C");
	    Consumer consumer = new DefaultConsumer(channel) {
	        @Override
	        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	            throws IOException {
	          String message = new String(body, "UTF-8");
	          logger.info(PAYMENTGATEWAY + " [x] Received '" + message + "'");
	        }
	      };
	      channel.basicConsume(QUEUE_NAME, true, consumer);
	    }
}
