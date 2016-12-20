package com.ktb.payment;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ktb.payment.model.PaymentTransaction;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class PaymentServiceTest {
	private final static Logger logger = LoggerFactory.getLogger(PaymentServiceTest.class);
	private static PaymentTransaction payment;
    public static String id;

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

//	@Test
	public void postDataPayment() {
		// init data test
		logger.info("testPostDataPayment : "+payment.toString());
		String param = "fromAcct=" + payment.getFromAccountNumber() + "&amount=" + payment.getAmount() + "&storeCode="
				+ payment.getStoreCode() + "&channel=" + payment.getChannel();

	    Response response = given().contentType("application/json").body(param).when().post().then()
				.statusCode(200).extract().response();
	    String jsonAsString = response.asString();
		String tmp = jsonAsString.substring(jsonAsString.indexOf("<label for=\"id\">"),jsonAsString.length());
		tmp = tmp.substring(0,tmp.indexOf("</label>"));
		id = tmp.replace("<label for=\"id\">Id: ", "");
		logger.info("id >>> "+id);
	}

	@Test
	public void checkStateRecv() {
		id = "7a27c6f8-05f2-458e-ae6e-c41cae117f94";
		logger.info("checkStateRecv : id [ "+id+" ]");
		Response response = given().when().get("/"+id).then()
				.statusCode(200)
//				.body("id", equalTo(id.toString()))
//				.body("fromAccountNumber", equalTo(payment.getFromAccountNumber()))
//				.body("amount", is(payment.getAmount()))
//				.body("storeCode", equalTo(payment.getStoreCode()))
//				.body("channel", equalTo(payment.getChannel()))
//				.assertThat()
				.extract().response();
		
		JsonPath jp = new JsonPath(response.asString());
		Assert.assertEquals("id from API doesn't match.",id, jp.get("id").toString());
		Assert.assertEquals("fromAccountNumber from API doesn't match.",payment.getFromAccountNumber(), jp.get("fromAccountNumber").toString());
//		Assert.assertEquals("amount from API doesn't match.",payment.getAmount(), Double.valueOf(jp.get("amount")));
		Assert.assertEquals("storeCode from API doesn't match.",payment.getStoreCode(), jp.get("storeCode").toString());
		Assert.assertEquals("channel from API doesn't match.",payment.getChannel(), jp.get("channel").toString());
		Assert.assertEquals("trxStatus from API doesn't match.","RECV", jp.get("trxStatus").toString());
	}
	
	@Test
	public void checkStateComp() {
		try {
			Thread.sleep((long) (3000));
			id = "7a27c6f8-05f2-458e-ae6e-c41cae117f94";
			logger.info("checkStateComp : id [ "+id+" ]");
			Response response = given().when().get("/"+id).then()
//					.body("fromAccountNumber", equalTo(payment.getFromAccountNumber()))
//					.body("amount", is(payment.getAmount()))
//					.body("storeCode", equalTo(payment.getStoreCode()))
//					.body("channel", equalTo(payment.getChannel()))
//					.body("trxStatus", equalTo("COMP"))
					.statusCode(200).extract().response();
			
			JsonPath jp = new JsonPath(response.asString());
			Assert.assertEquals("id from API doesn't match.",id, jp.get("id").toString());
			Assert.assertEquals("fromAccountNumber from API doesn't match.",payment.getFromAccountNumber(), jp.get("fromAccountNumber").toString());
//			Assert.assertEquals("amount from API doesn't match.",payment.getAmount(), Double.valueOf(jp.get("amount")));
			Assert.assertEquals("storeCode from API doesn't match.",payment.getStoreCode(), jp.get("storeCode").toString());
			Assert.assertEquals("channel from API doesn't match.",payment.getChannel(), jp.get("channel").toString());
			Assert.assertEquals("trxStatus from API doesn't match.","COMP", jp.get("trxStatus").toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
