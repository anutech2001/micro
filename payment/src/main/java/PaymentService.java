
import static spark.Spark.post;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;;

public class PaymentService {
	private final static Logger logger = LoggerFactory.getLogger(PaymentService.class);
	private final static String QUEUE_NAME = "paymentTo3rdCreated";
	private final static String PAYMENT = "Payment Service:";

	public static void main(final String[] args) {

		post("/payment", (request, response) -> {
			// validate input data
			java.util.Date dateTime = Calendar.getInstance().getTime();
			logger.info(PAYMENT + " received payment transaction - '" + request.body() + "'");
			logger.info(PAYMENT + " validating transcation data.");
			Thread.sleep((long) (randomWithRange(1, 2) * 1000));
			logger.info(PAYMENT + " validating transcation data - PASS. ");
			logger.info(PAYMENT + " checking account.");
			Thread.sleep((long) (randomWithRange(1, 2) * 1000));
			logger.info(PAYMENT + " checking account - PASS. ");
			logger.info(PAYMENT + " pay to other organiztion, sent event to Payment Gateway Service. ");
			PaymentTransaction pt = getPaymentTransaction(request.body());
			pt.setTrxDateTime(dateTime.toString());
			send(encode(pt));
			Map<String, Object> model = new HashMap<>();
			model.put("paymentTrx", pt);
			return new ModelAndView(model, "paymentResponse.vm");
		}, new VelocityTemplateEngine());

	}
	
	public static String encode(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	static int randomWithRange(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

	public static void send(String msg) {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(System.getenv("RabbitHost"));
		factory.setPort(5672);
		factory.setUsername("guest");
		factory.setPassword("guest");
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

	static PaymentTransaction getPaymentTransaction(String req) {
		return conv(parseReq(req));
	}

	static Map<String, String> parseReq(String req) {
		StringTokenizer st = new StringTokenizer(req, "&");
		Map<String, String> reqMap = new HashMap<String, String>();
		while (st.hasMoreElements()) {
			String attr = st.nextToken();
			String[] kv = attr.split("=");
			reqMap.put(kv[0], kv[1]);
		}
		return reqMap;
	}

	static PaymentTransaction conv(Map<String, String> map) {
		PaymentTransaction pt = new PaymentTransaction();
		pt.setFromAccountNumber(map.get("fromAcct"));
		pt.setAmount(Double.valueOf(map.get("amount")));
		pt.setStoreCode(map.get("storeCode"));
		return pt;
	}

	public static class PaymentTransaction {
		private String fromAccountNumber;
		private double amount;
		private String storeCode;
		private String trxDateTime;

		public String getTrxDateTime() {
			return trxDateTime;
		}

		public void setTrxDateTime(String trxDateTime) {
			this.trxDateTime = trxDateTime;
		}

		public String getFromAccountNumber() {
			return fromAccountNumber;
		}

		public void setFromAccountNumber(String fromAccountNumber) {
			this.fromAccountNumber = fromAccountNumber;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public String getStoreCode() {
			return storeCode;
		}

		public void setStoreCode(String storeCode) {
			this.storeCode = storeCode;
		}
	}
}
