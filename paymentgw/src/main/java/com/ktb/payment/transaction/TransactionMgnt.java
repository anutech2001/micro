package com.ktb.payment.transaction;

import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.codehaus.jackson.map.ObjectMapper;

import com.ktb.payment.model.PaymentTransaction;

public class TransactionMgnt {
	static final String UNIT_NAME = "MariaDB-JPA";
	static final String STATUS_COMP = "COMP";
	static final String GATEWAY = "GATEWAY";
	
	public static void updatePaymentTransaction(String message){
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory(UNIT_NAME);
			em = emf.createEntityManager();
			em.getTransaction().begin( );
			
			PaymentTransaction transaction = decodePaymentTransacion(message);
			
			PaymentTransaction paymentTransaction = em.find(PaymentTransaction.class, transaction.getId());
			paymentTransaction.setTrxStatus(STATUS_COMP);
			paymentTransaction.setUpdatedBy(GATEWAY);
			paymentTransaction.setUpdatedDate(Calendar.getInstance().getTime());
			
			em.persist(paymentTransaction);
		    em.getTransaction().commit( );
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			em.close();
			emf.close();
		}
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
}
