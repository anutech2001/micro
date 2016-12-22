package com.ktb.payment.monitor;

import static j2html.TagCreator.article;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;
import static spark.Spark.webSocketIdleTimeoutMillis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.event.handler.EventConfig;
import com.ktb.payment.event.handler.EventHandler;

public class PaymentMonitor {
	private final static Logger logger = LoggerFactory.getLogger(PaymentMonitor.class);
	private final static String RABBITMQ_HOST = "RABBITMQ_HOST";
	private final static String RABBITMQ_PORT = "RABBITMQ_PORT";
	private final static String RABBITMQ_USER = "RABBITMQ_USER";
	private final static String RABBITMQ_PASS = "RABBITMQ_PASS";
	private final static int WEBSOCKET_TIMEOUT = 20*60*1000;
	static List<Session> sessionList = new ArrayList<>();

	public static void main(String[] args) {
		logger.info("==============================================================");
		logger.info("Payment Monitor Config: RabbitMQ Host = " + System.getenv(RABBITMQ_HOST));
		logger.info("                		 RabbitMQ Port = " + System.getenv(RABBITMQ_PORT));
		logger.info("                		 RabbitMQ User = " + System.getenv(RABBITMQ_USER));
		logger.info("==============================================================");
		// initial RabbitMQ configuration
		new EventConfig(System.getenv(RABBITMQ_HOST), System.getenv(RABBITMQ_PORT), System.getenv(RABBITMQ_USER),
				System.getenv(RABBITMQ_PASS));

		// wait for an event
		EventHandler.handlePaymentTo3rdCompletedEvent();		

		port(9000);
		staticFiles.location("/public");
		staticFiles.expireTime(1);
		webSocket("/pmon", MonitorHandler.class);
		webSocketIdleTimeoutMillis(WEBSOCKET_TIMEOUT);
		init();
	}		

	// Send a message to monitoring user
	public static void broadcastMessage(String message) {
		sessionList.forEach(session -> {
			try {
				session.getRemote().sendString(
						String.valueOf(new JSONObject().put("userMessage", createHtmlMessageFromSender(message))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// Builds a HTML element with a message, and a time stamp.
	private static String createHtmlMessageFromSender(String message) {
		return article()
				.with(b("Payment Transaction: "), p(message),
						span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date())))
				.render();
	}
}
