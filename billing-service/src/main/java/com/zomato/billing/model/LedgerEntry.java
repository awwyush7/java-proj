package com.zomato.billing.model;

import java.math.BigDecimal;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

// One row in DynamoDB. The AWS library needs an empty constructor plus getters and setters.
@DynamoDbBean
public class LedgerEntry {

	private String orderId;
	private String entryType;
	private String merchantId;
	private String serviceId;
	private BigDecimal amount;

	@DynamoDbPartitionKey
	public String getOrderId() { return orderId; }
	public void setOrderId(String orderId) { this.orderId = orderId; }

	@DynamoDbSortKey
	public String getEntryType() { return entryType; }
	public void setEntryType(String entryType) { this.entryType = entryType; }

	public String getMerchantId() { return merchantId; }
	public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

	public String getServiceId() { return serviceId; }
	public void setServiceId(String serviceId) { this.serviceId = serviceId; }

	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
}
