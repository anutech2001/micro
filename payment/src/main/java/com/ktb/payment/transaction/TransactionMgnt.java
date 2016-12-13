package com.ktb.payment.transaction;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ktb.payment.model.PaymentTransaction;

public class TransactionMgnt {
	static final String STATUS_RECV = "RECV";
	static final String UNIT_NAME = "MariaDB-JPA";

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
		pt.setChannel(map.get("channel"));
		return pt;
	}

	public static PaymentTransaction createPaymentTransaction(String messageBody){
		java.util.Date dateTime = Calendar.getInstance().getTime();
		PaymentTransaction pt = getPaymentTransaction(messageBody);
		pt.setTrxDateTime(dateTime);
		pt.setId(String.valueOf(UUID.randomUUID()));
		pt.setTrxStatus(STATUS_RECV);
		pt.setCreatedBy(pt.getChannel());
		pt.setCreatedDate(dateTime);
		
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			em = emf.createEntityManager();
			em.getTransaction().begin( );
			
			em.persist(pt);
		    em.getTransaction().commit( );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			em.close();
			emf.close();
		}
		return pt;
	}
}
