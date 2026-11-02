package com.zomato.billing.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.zomato.billing.model.LedgerEntry;
import com.zomato.billing.model.OrderEvent;

class BreakupCalculatorTest {

	@Test
	void splitsOrderIntoCommissionAndPayout() {
		OrderEvent order = new OrderEvent("ORD-1", "M-1", "S-1", "DELIVERED", new BigDecimal("100"), new BigDecimal("30"));

		List<LedgerEntry> entries = new BreakupCalculator(new BigDecimal("20")).calculate(order);

		assertEquals(new BigDecimal("20.00"), entries.get(0).getAmount());   // commission
		assertEquals(new BigDecimal("30"), entries.get(1).getAmount());      // delivery fee
		assertEquals(new BigDecimal("80.00"), entries.get(2).getAmount());   // merchant payout
	}
}
