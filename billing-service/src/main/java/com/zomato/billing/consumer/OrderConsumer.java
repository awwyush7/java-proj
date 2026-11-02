package com.zomato.billing.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.zomato.billing.model.OrderEvent;
import com.zomato.billing.service.BillingService;

import tools.jackson.databind.ObjectMapper;

@Component
public class OrderConsumer {

	private final BillingService billingService;
	private final ObjectMapper objectMapper;

	public OrderConsumer(BillingService billingService, ObjectMapper objectMapper) {
		this.billingService = billingService;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "${app.order-topic}")
	public void consume(String message) {
		OrderEvent order = objectMapper.readValue(message, OrderEvent.class);
		if ("DELIVERED".equals(order.status())) {
			billingService.bill(order);
		}
	}
}
