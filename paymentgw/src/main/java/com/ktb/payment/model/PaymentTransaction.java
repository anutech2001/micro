package com.ktb.payment.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="payment_transaction")
public class PaymentTransaction {
	@Column(name="from_account_number")
	private String fromAccountNumber;
	private double amount;
	@Column(name="store_code")
	private String storeCode;
	@Column(name="trx_date_time")
	private Date trxDateTime;
	//TODO add trxStatus
	/**
	 * insert status = receive(RECV)
	 * 
	 * @return
	 */
	//TODO add channel
	//TOTO add createdDate
	//TODO add createdBy
	@Column(name="trx_status")
	private String trxStatus;
	private String channel;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="created_date")
	private Date createdDate;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="updated_date")
	private Date updatedDate;
	@Id
	private String id;
	
	public PaymentTransaction(){}

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

	public Date getTrxDateTime() {
		return trxDateTime;
	}

	public void setTrxDateTime(Date trxDateTime) {
		this.trxDateTime = trxDateTime;
	}

	public String getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
