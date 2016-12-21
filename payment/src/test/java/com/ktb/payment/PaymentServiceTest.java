package com.ktb.payment;

import static io.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.model.PaymentTransaction;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class PaymentServiceTest {
	private final static Logger logger = LoggerFactory.getLogger(PaymentServiceTest.class);
	private static PaymentTransaction payment;

	@Test
	public void testCallPayment() {
		given().when().get("http://localhost:4567/payment").then().statusCode(200);
	}

	@BeforeClass
	public static void initDataPayment() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8999;
		RestAssured.basePath = "/payment";

		payment = new PaymentTransaction();
		payment.setFromAccountNumber("1001");
		payment.setAmount(Double.valueOf("555"));
		payment.setStoreCode("store02");
		payment.setChannel("FRONT");
	}

	@Test
	public void postDataPayment() {
		// init data test
		logger.info("testPostDataPayment : " + payment.toString());
		String param = "fromAcct=" + payment.getFromAccountNumber() + "&amount=" + payment.getAmount() + "&storeCode="
				+ payment.getStoreCode() + "&channel=" + payment.getChannel();

		Response response = given().contentType("application/json").body(param).when().post().then().statusCode(200)
				.extract().response();
		String jsonAsString = response.asString();
		String tmp = jsonAsString.substring(jsonAsString.indexOf("<label for=\"id\">"), jsonAsString.length());
		tmp = tmp.substring(0, tmp.indexOf("</label>"));
		payment.setId(tmp.replace("<label for=\"id\">Id: ", ""));
	}

	@Test
	public void checkStateRecv() {
		try {
			logger.info("checkStateRecv : id [ " + payment.getId() + " ]");
			Response response = given().when().get("/" + payment.getId()).then().statusCode(200).extract().response();

			JsonPath jp = new JsonPath(response.asString());
			Assert.assertEquals("id from API doesn't match.", payment.getId(), jp.get("id").toString());
			Assert.assertEquals("fromAccountNumber from API doesn't match.", payment.getFromAccountNumber(),
					jp.get("fromAccountNumber").toString());
			Assert.assertEquals("amount from API doesn't match.", Double.doubleToLongBits(payment.getAmount()),
					Double.doubleToLongBits(Double.valueOf(jp.get("amount"))));
			Assert.assertEquals("storeCode from API doesn't match.", payment.getStoreCode(),
					jp.get("storeCode").toString());
			Assert.assertEquals("channel from API doesn't match.", payment.getChannel(), jp.get("channel").toString());
			Assert.assertEquals("trxStatus from API doesn't match.", "RECV", jp.get("trxStatus").toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Test
	public void checkStateComp() {
		try {
			Thread.sleep((long) (5000));
			logger.info("checkStateComp : id [ " + payment.getId() + " ]");
			Response response = given().when().get("/" + payment.getId()).then().statusCode(200).extract().response();

			JsonPath jp = new JsonPath(response.asString());
			Assert.assertEquals("id from API doesn't match.", payment.getId(), jp.get("id").toString());
			Assert.assertEquals("fromAccountNumber from API doesn't match.", payment.getFromAccountNumber(),
					jp.get("fromAccountNumber").toString());
			Assert.assertEquals("amount from API doesn't match.", Double.doubleToLongBits(payment.getAmount()),
					Double.doubleToLongBits(Double.valueOf(jp.get("amount"))));
			Assert.assertEquals("storeCode from API doesn't match.", payment.getStoreCode(),
					jp.get("storeCode").toString());
			Assert.assertEquals("channel from API doesn't match.", payment.getChannel(), jp.get("channel").toString());
			Assert.assertEquals("trxStatus from API doesn't match.", "COMP", jp.get("trxStatus").toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
