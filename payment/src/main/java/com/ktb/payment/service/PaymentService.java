package com.ktb.payment.service;

import static spark.Spark.post;
import static spark.Spark.port;
import static spark.Spark.get;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ktb.payment.model.PaymentTransaction;
import com.ktb.payment.transaction.DBConfig;
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
	private final static String PAYMENT_DB_HOST = "PAYMENT_DB_HOST";
	private final static String PAYMENT_DB_NAME = "PAYMENT_DB_NAME";
	private final static String PAYMENT_DB_PORT = "PAYMENT_DB_PORT";
	private final static String PAYMENT_DB_USER = "PAYMENT_DB_USER";
	private final static String PAYMENT_DB_PASS = "PAYMENT_DB_PASS";
	

	public static void main(final String[] args) {
		logger.info("==============================================================");
		logger.info("Payment Config: Database Host = " + System.getenv(PAYMENT_DB_HOST));
		logger.info("                Database Name = " + System.getenv(PAYMENT_DB_NAME));
		logger.info("                Database Port = " + System.getenv(PAYMENT_DB_PORT));
		logger.info("                Database User = " + System.getenv(PAYMENT_DB_USER));
		logger.info("==============================================================");
		new DBConfig(System.getenv(PAYMENT_DB_HOST),System.getenv(PAYMENT_DB_NAME),
				     System.getenv(PAYMENT_DB_PORT),System.getenv(PAYMENT_DB_USER),
					 System.getenv(PAYMENT_DB_PASS));
		
		String paymentPort = System.getenv(PAYMENT_PORT);
		if(null != paymentPort){
			port(Integer.parseInt(paymentPort)); 
		}
		get("/payment/:fromAccountNumber/:amount/:storeCode/:channel","application/json", (request, response) -> {
			String param = "fromAcct="+request.params(":fromAccountNumber")
							+"&amount="+request.params(":amount")
							+"&storeCode="+request.params(":storeCode")
							+"&channel="+request.params(":channel");
			List<PaymentTransaction> list = TransactionMgnt.findPaymentTransactions(param);
//			PaymentTransaction paymentTransaction = TransactionMgnt.findPaymentTransaction(param);
			return list;
		}, new JsonTransformer());
		
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
