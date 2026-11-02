package com.zomato.billing.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zomato.billing.calculator.BreakupCalculator;
import com.zomato.billing.model.LedgerEntry;
import com.zomato.billing.model.OrderEvent;
import com.zomato.billing.repository.LedgerRepository;

@Service
public class BillingService {

	private static final Logger log = LoggerFactory.getLogger(BillingService.class);

	private final BreakupCalculator calculator;
	private final LedgerRepository repository;

	public BillingService(BreakupCalculator calculator, LedgerRepository repository) {
		this.calculator = calculator;
		this.repository = repository;
	}

	public void bill(OrderEvent order) {
		List<LedgerEntry> entries = calculator.calculate(order);
		boolean saved = repository.saveIfNew(entries);
		log.info("Order {}: {}", order.orderId(), saved ? "billed" : "already billed, skipped");
	}
}
