package com.zomato.billing.model;

import java.math.BigDecimal;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

// One row in DynamoDB. The AWS library needs an empty constructor plus getters and setters.
@DynamoDbBean
public class LedgerEntry {

	private String orderId;
	private String ledgerId;
	private OrderStatusEnum orderStatus;
	private String merchantId;
	private String serviceId;
	private java.time.Instant orderTime;
	private BigDecimal amount;
	private LedgerAccountEnum ledgerAccount;
	private LedgerTypeEnum ledgerType;
	// private String paymentEntity;

	@DynamoDbPartitionKey
	public String getOrderId() { return orderId; }
	public void setOrderId(String orderId) { this.orderId = orderId; }

	@DynamoDbSortKey
	public String getLedgerId() { return ledgerId; }
	public void setLedgerId(String ledgerId) { this.ledgerId = ledgerId; }

	public java.time.Instant getOrderTime() { return orderTime; }
	public void setOrderTime(java.time.Instant orderTime) {this.orderTime = orderTime;}

	public OrderStatusEnum getOrderStatus() { return orderStatus; }
	public void setOrderStatus(OrderStatusEnum orderStatus) { this.orderStatus = orderStatus; }

	public String getMerchantId() { return merchantId; }
	public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

	public LedgerAccountEnum getLedgerAccount() { return ledgerAccount; }
	public void setLedgerAccount(LedgerAccountEnum ledgerAccount) { this.ledgerAccount = ledgerAccount; }

	public String getServiceId() { return serviceId; }
	public void setServiceId(String serviceId) { this.serviceId = serviceId; }

	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }

	public LedgerTypeEnum getLedgerType() {return ledgerType;}
	public void setLedgerType(LedgerTypeEnum ledgerType) {this.ledgerType = ledgerType;}

	// public String getPaymentEntity() {return paymentEntity;}
	// public void setPaymentEntity(String paymentEntity) {this.paymentEntity = paymentEntity;}
}
