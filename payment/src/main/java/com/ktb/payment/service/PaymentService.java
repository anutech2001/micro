package com.ktb.payment.service;

import static spark.Spark.post;
import static spark.Spark.port;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ktb.payment.model.PaymentTransaction;
import com.ktb.payment.transaction.TransactionMgnt;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;;

public class PaymentService {
	private final static Logger logger = LoggerFactory.getLogger(PaymentService.class);
	private final static String QUEUE_NAME = "paymentTo3rdCreated";
	private final static String PAYMENT = "Payment Service:";
	private final static String PAYMENT_PORT = "PaymentPort";
	private final static String PAYMENT_HOST = "RabbitHost";
	private final static String PAYMENT_USER = "guest";
	private final static String PAYMENT_PASS = "guest";

	public static void main(final String[] args) {
		String paymentPort = System.getenv(PAYMENT_PORT);
		if(null != paymentPort){
			port(Integer.parseInt(paymentPort));
		}
		post("/payment", (request, response) -> {
		
			// validate input data
			logger.info(PAYMENT + " received payment transaction - '" + request.body() + "'");
			logger.info(PAYMENT + " validating transcation data.");
			Thread.sleep((long) (randomWithRange(1, 2) * 1000));
			logger.info(PAYMENT + " validating transcation data - PASS. ");
			logger.info(PAYMENT + " checking account.");
			Thread.sleep((long) (randomWithRange(1, 2) * 1000));
			logger.info(PAYMENT + " checking account - PASS. ");
			logger.info(PAYMENT + " pay to other organiztion, sent event to Payment Gateway Service. ");
			
			PaymentTransaction pt = TransactionMgnt.createPaymentTransaction(request.body());
			
			send(TransactionMgnt.encode(pt));
			Map<String, Object> model = new HashMap<>();
			model.put("paymentTrx", pt);
			return new ModelAndView(model, "paymentResponse.vm");
		}, new VelocityTemplateEngine());

	}
	
	public static void send(String msg) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(System.getenv(PAYMENT_HOST));
		factory.setPort(5672);
		factory.setUsername(PAYMENT_USER);
		factory.setPassword(PAYMENT_PASS);
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
			logger.info(" [x] Sent '" + msg +"'");
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	static int randomWithRange(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}
}