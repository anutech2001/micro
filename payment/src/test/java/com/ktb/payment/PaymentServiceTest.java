package com.ktb.payment;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.model.PaymentTransaction;
import com.ktb.payment.service.PaymentService;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class PaymentServiceTest {
	private final static Logger logger = LoggerFactory.getLogger(PaymentServiceTest.class);
	private static PaymentTransaction payment;

	// @Test
	// public void testCallPayment(){
	// given().when().get("http://localhost:8102/payment").then().statusCode(200);
	// }
	
	@BeforeClass
	public static void initDataPayment(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 4567;
		RestAssured.basePath = "/payment";
		
		payment = new PaymentTransaction();
		payment.setFromAccountNumber("act002");
		payment.setAmount(Double.valueOf("99999"));
		payment.setStoreCode("store01");
		payment.setChannel("FRONT");
	}

	@Test
	public void postDataPayment() {
		// init data test
		logger.info("testPostDataPayment : "+payment.toString());
		String param = "fromAcct=" + payment.getFromAccountNumber() + "&amount=" + payment.getAmount() + "&storeCode="
				+ payment.getStoreCode() + "&channel=" + payment.getChannel();

		given().contentType("application/json").body(param).when().post("http://localhost:4567/payment").then()
				.statusCode(200);
	}

	@Test
	public void checkStateRecv() {
		logger.info("checkStateRecv : "+payment.toString());
		String paramGet = "/" + payment.getFromAccountNumber() + "/" + payment.getAmount() + "/"
				+ payment.getStoreCode() + "/" + payment.getChannel();
		given().when().get(paramGet).then()
				.body("fromAccountNumber", equalTo(payment.getFromAccountNumber()))
				.body("amount", is(payment.getAmount()))
				.body("storeCode", equalTo(payment.getStoreCode()))
				.body("channel", equalTo(payment.getChannel()))
				.body("trxStatus", equalTo("RECV"))
				.statusCode(200);
	}
	
	@Test
	public void checkStateComp() {
		logger.info("checkStateRecv : "+payment.toString());
		String paramGet = "/" + payment.getFromAccountNumber() + "/" + payment.getAmount() + "/"
				+ payment.getStoreCode() + "/" + payment.getChannel();
		given().when().get(paramGet).then()
				.body("fromAccountNumber", equalTo(payment.getFromAccountNumber()))
				.body("amount", is(payment.getAmount()))
				.body("storeCode", equalTo(payment.getStoreCode()))
				.body("channel", equalTo(payment.getChannel()))
				.body("trxStatus", equalTo("COMP"))
				.statusCode(200);
	}
}
