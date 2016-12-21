package com.ktb.payment.transaction;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktb.payment.model.PaymentTransaction;

public class TransactionMgnt {
	private final static Logger logger = LoggerFactory.getLogger(TransactionMgnt.class);
	static final String STATUS_RECV = "RECV";
	static final String UNIT_NAME = "MariaDB-JPA";
	static Map<String, String> jpaProperties;
	
	
	static {
		jpaProperties = new HashMap<String, String>();
		jpaProperties.put("javax.persistence.jdbc.user", DBConfig.getDbUser());
		jpaProperties.put("javax.persistence.jdbc.password", DBConfig.getDbPassword());
		jpaProperties.put("javax.persistence.jdbc.url", "jdbc:mariadb://"+DBConfig.getDbHost() + 
																		":" + DBConfig.getDbPort() +
																		"/" + DBConfig.getDbName());				
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
//		java.util.Date dateTime = Calendar.getInstance().getTime();
		java.util.Date dateTime = new Date();
		PaymentTransaction pt = getPaymentTransaction(messageBody);
		pt.setTrxDateTime(dateTime);
		pt.setId(String.valueOf(UUID.randomUUID()));
		pt.setTrxStatus(STATUS_RECV);
		pt.setCreatedBy(pt.getChannel());
		pt.setCreatedDate(dateTime);
		
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory(UNIT_NAME, jpaProperties);
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
	
	public static List<PaymentTransaction> findPaymentTransactions(String messageBody){
		logger.info("messageBody >>> "+messageBody);
		List<PaymentTransaction> paymentTransactions = null;
		PaymentTransaction pt = getPaymentTransaction(messageBody);
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory(UNIT_NAME, jpaProperties);
			em = emf.createEntityManager();
			StringBuilder sql = new StringBuilder();
			sql.append(" select a from PaymentTransaction a ");
			sql.append(" where a.fromAccountNumber = :fromAccountNumber ");
//			sql.append(" and a.amount = :amount ");
			sql.append(" and a.storeCode = :storeCode ");
			sql.append(" and a.channel = :channel");
			logger.info("sql >>> "+sql.toString());
			TypedQuery<PaymentTransaction> query = em.createQuery(sql.toString(),PaymentTransaction.class);
			query.setParameter("fromAccountNumber", pt.getFromAccountNumber());
//			query.setParameter("amount", pt.getAmount());
			query.setParameter("storeCode", pt.getStoreCode());
			query.setParameter("channel", pt.getChannel());
			
			paymentTransactions = query.getResultList();
			logger.info("paymentTransactions >>> "+paymentTransactions);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			em.close();
			emf.close();
		}
		return paymentTransactions;
	}
	
	public static PaymentTransaction findPaymentTransaction(String id){
		PaymentTransaction transaction = null;
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			em = emf.createEntityManager();
			transaction = em.find(PaymentTransaction.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			em.close();
			emf.close();
		}
		return transaction;
	}
}
