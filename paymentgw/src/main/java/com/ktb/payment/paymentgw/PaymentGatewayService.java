package com.ktb.payment.paymentgw;

import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ktb.payment.event.EventConfig;
import com.ktb.payment.event.EventHandler;

public class PaymentGatewayService {
	private final static Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);
	private final static String PAYMENT_DB_HOST = "PAYMENT_DB_HOST";
	private final static String PAYMENT_DB_NAME = "PAYMENT_DB_NAME";
	private final static String PAYMENT_DB_PORT = "PAYMENT_DB_PORT";
	private final static String PAYMENT_DB_USER = "PAYMENT_DB_USER";
	private final static String PAYMENT_DB_PASS = "PAYMENT_DB_PASS";
	private final static String RABBITMQ_HOST = "RABBITMQ_HOST";
	private final static String RABBITMQ_PORT = "RABBITMQ_PORT";
	private final static String RABBITMQ_USER = "RABBITMQ_USER";
	private final static String RABBITMQ_PASS = "RABBITMQ_PASS";

	public static void main(String[] argv)
			throws java.io.IOException, java.lang.InterruptedException, TimeoutException {

		logger.info("==============================================================");
		logger.info("Payment Config: Database Host = " + System.getenv(PAYMENT_DB_HOST));
		logger.info("                Database Name = " + System.getenv(PAYMENT_DB_NAME));
		logger.info("                Database Port = " + System.getenv(PAYMENT_DB_PORT));
		logger.info("                Database User = " + System.getenv(PAYMENT_DB_USER));
		logger.info("                RabbitMQ Host = " + System.getenv(RABBITMQ_HOST));
		logger.info("                RabbitMQ Port = " + System.getenv(RABBITMQ_PORT));
		logger.info("                RabbitMQ User = " + System.getenv(RABBITMQ_USER));
		logger.info("==============================================================");
		new DBConfig(System.getenv(PAYMENT_DB_HOST),System.getenv(PAYMENT_DB_NAME),
				     System.getenv(PAYMENT_DB_PORT),System.getenv(PAYMENT_DB_USER),
					 System.getenv(PAYMENT_DB_PASS));
		
		// initial RabbitMQ configuration
		new EventConfig(System.getenv(RABBITMQ_HOST), System.getenv(RABBITMQ_PORT), System.getenv(RABBITMQ_USER),
				System.getenv(RABBITMQ_PASS));
		
		// publish an event
		logger.info(" pay to other organiztion, sent event to Payment Gateway Service. ");
		EventHandler.paymentTo3rdCreatedEvent();
	}
}
