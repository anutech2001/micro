package com.ktb.payment.service;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.event.EventConfig;
import com.ktb.payment.event.EventHandler;
import com.ktb.payment.model.PaymentTransaction;
import com.ktb.payment.transaction.DBConfig;
import com.ktb.payment.transaction.TransactionMgnt;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;;

public class PaymentService {
	private final static Logger logger = LoggerFactory.getLogger(PaymentService.class);
	private final static String PAYMENT = "Payment Service:";
	private final static String PAYMENT_PORT = "PAYMENT_PORT";
	
	private final static String RABBITMQ_HOST = "RABBITMQ_HOST";
	private final static String RABBITMQ_PORT = "RABBITMQ_PORT";
	private final static String RABBITMQ_USER = "RABBITMQ_USER";
	private final static String RABBITMQ_PASS = "RABBITMQ_PASS";
	
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
		logger.info("                RabbitMQ Host = " + System.getenv(RABBITMQ_HOST));
		logger.info("                RabbitMQ Port = " + System.getenv(RABBITMQ_PORT));
		logger.info("                RabbitMQ User = " + System.getenv(RABBITMQ_USER));
		logger.info("==============================================================");
		
		// initial database configuration
		new DBConfig(System.getenv(PAYMENT_DB_HOST),System.getenv(PAYMENT_DB_NAME),
				     System.getenv(PAYMENT_DB_PORT),System.getenv(PAYMENT_DB_USER),
					 System.getenv(PAYMENT_DB_PASS));
		
		// initial RabbitMQ configuration
		new EventConfig(System.getenv(RABBITMQ_HOST),System.getenv(RABBITMQ_PORT),
			     System.getenv(RABBITMQ_USER),System.getenv(RABBITMQ_PASS));
		
		// set port of Payment Services, default is 4567
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
		
		// create payment
		post("/payment", (request, response) -> {
			// validate input data
			logger.info(PAYMENT + " received payment transaction - '" + request.body() + "'");
			logger.info(PAYMENT + " validating transcation data.");
			Thread.sleep((long) (randomWithRange(1, 2) * 1000));
			logger.info(PAYMENT + " validating transcation data - PASS. ");
			logger.info(PAYMENT + " checking account.");
			Thread.sleep((long) (randomWithRange(1, 2) * 1000));
			logger.info(PAYMENT + " checking account - PASS. ");
			
			// Save transaction to database
			logger.info(PAYMENT + " save payment transaction record. ");
			PaymentTransaction pt = TransactionMgnt.createPaymentTransaction(request.body());
			
			// publish an event
			logger.info(PAYMENT + " pay to other organiztion, sent event to Payment Gateway Service. ");
			EventHandler.paymentTo3rdCreatedEvent(TransactionMgnt.encode(pt));
			
			// return response page for user
			Map<String, Object> model = new HashMap<>();
			model.put("paymentTrx", pt);
			return new ModelAndView(model, "paymentResponse.vm");
		}, new VelocityTemplateEngine());

	}
	

	static int randomWithRange(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}
}
