package com.zomato.billing.model;

import java.math.BigDecimal;

public record OrderEvent(String orderId, String merchantId, String serviceId, String status,
		BigDecimal itemTotal, BigDecimal deliveryFee) {
}
