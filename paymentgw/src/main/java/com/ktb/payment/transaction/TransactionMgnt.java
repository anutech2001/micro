package com.ktb.payment.transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.codehaus.jackson.map.ObjectMapper;

import com.ktb.payment.model.PaymentTransaction;
import com.ktb.payment.paymentgw.DBConfig;

public class TransactionMgnt {
	static final String UNIT_NAME = "MariaDB-JPA";
	static final String STATUS_COMP = "COMP";
	static final String GATEWAY = "GATEWAY";
	static Map<String, String> jpaProperties;
	
	static {
		jpaProperties = new HashMap<String, String>();
		jpaProperties.put("javax.persistence.jdbc.user", DBConfig.getDbUser());
		jpaProperties.put("javax.persistence.jdbc.password", DBConfig.getDbPassword());
		jpaProperties.put("javax.persistence.jdbc.url", "jdbc:mariadb://"+DBConfig.getDbHost() + 
																		":" + DBConfig.getDbPort() +
																		"/" + DBConfig.getDbName());				
	}
	
	public static PaymentTransaction updatePaymentTransaction(String message){
		EntityManagerFactory emf = null;
		EntityManager em = null;
		PaymentTransaction trx = null;
		try {
			emf = Persistence.createEntityManagerFactory(UNIT_NAME, jpaProperties);
			em = emf.createEntityManager();
			em.getTransaction().begin( );
			
			PaymentTransaction transaction = decodePaymentTransacion(message);
			
			PaymentTransaction paymentTransaction = em.find(PaymentTransaction.class, transaction.getId());
			paymentTransaction.setTrxStatus(STATUS_COMP);
			paymentTransaction.setUpdatedBy(GATEWAY);
			paymentTransaction.setUpdatedDate(Calendar.getInstance().getTime());
			
			em.persist(paymentTransaction);
		    em.getTransaction().commit( );
		    trx = paymentTransaction;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			em.close();
			emf.close();
		}
	    return trx;
	}
	
	public static PaymentTransaction decodePaymentTransacion(String arg){
		PaymentTransaction transaction = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			transaction = mapper.readValue(arg, PaymentTransaction.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return transaction;
	}
	
	public static String encodePaymentTransacion(PaymentTransaction trx) {
		ObjectMapper mapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS a z");
		mapper.setDateFormat(df);
		String trxStr = null;
		try {
			trxStr = mapper.writeValueAsString(trx);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return trxStr;
	}
}
