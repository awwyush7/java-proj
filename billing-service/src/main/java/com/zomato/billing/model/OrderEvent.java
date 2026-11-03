package com.zomato.billing.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

// COMING FROM THE GO SERVICE - SO customer facing: Whatever customer sees as breakup
public record OrderEvent(
		@JsonProperty("order_id") String orderId,
		@JsonProperty("merchant_id") String merchantId,
		@JsonProperty("service_id") String serviceId,
		@JsonProperty("status") OrderStatusEnum status,
		@JsonProperty("total_amount") BigDecimal totalAmount,
		@JsonProperty("food_value") BigDecimal foodValue,
		@JsonProperty("tax") BigDecimal tax,
		@JsonProperty("platform_fee") BigDecimal platformFee,
		@JsonProperty("order_time") java.time.Instant orderTime,
		@JsonProperty("delivery_fee") BigDecimal deliveryFee){
}
