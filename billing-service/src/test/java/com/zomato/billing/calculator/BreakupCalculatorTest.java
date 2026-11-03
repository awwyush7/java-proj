package com.zomato.billing.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.zomato.billing.model.LedgerAccountEnum;
import com.zomato.billing.model.LedgerEntry;
import com.zomato.billing.model.LedgerTypeEnum;
import com.zomato.billing.model.OrderEvent;
import com.zomato.billing.model.OrderStatusEnum;

class BreakupCalculatorTest {

	private final BreakupCalculator calculator = new BreakupCalculator(new BigDecimal("20"), new BigDecimal("10"));

	@Test
	void splitsOrderIntoLedgerEntries() {
		List<LedgerEntry> entries = calculator.calculate(order("86160", "65800", "14360", "1000", "5000"));

		assertEquals(7, entries.size());
		assertEquals(7, entries.stream().map(LedgerEntry::getLedgerId).distinct().count());
		assertEquals("LEDGER#ORD-1#TOTAL", entries.get(0).getLedgerId());
		assertEquals(new BigDecimal("77544"), amount(entries, LedgerAccountEnum.TOTAL));
		assertEquals(new BigDecimal("8616"), amount(entries, LedgerAccountEnum.PAYMENT_GATEWAY_FEE));
		assertEquals(new BigDecimal("14360"), amount(entries, LedgerAccountEnum.TAX));
		assertEquals(new BigDecimal("1000"), amount(entries, LedgerAccountEnum.PLATFORM_FEE));
		assertEquals(new BigDecimal("13160"), amount(entries, LedgerAccountEnum.COMMISSION));
		assertEquals(new BigDecimal("52640"), amount(entries, LedgerAccountEnum.RESTAURANT_PAYABLE));
		assertEquals(new BigDecimal("5000"), amount(entries, LedgerAccountEnum.DELIVERY_PAYABLE));
		assertEquals("M-1", entries.get(0).getMerchantId());
		assertEquals("S-1", entries.get(0).getServiceId());
	}

	@Test
	void debitsEqualCreditsEvenWhenAmountsRound() {
		// 10% of 4207 and 20% of 703 are both fractions of a paisa.
		List<LedgerEntry> entries = calculator.calculate(order("4207", "703", "701", "503", "2300"));

		assertEquals(sum(entries, LedgerTypeEnum.DEBIT), sum(entries, LedgerTypeEnum.CREDIT));
		assertEquals(new BigDecimal("4207"), sum(entries, LedgerTypeEnum.DEBIT));
	}

	private OrderEvent order(String total, String food, String tax, String platformFee, String deliveryFee) {
		return new OrderEvent("ORD-1", "M-1", "S-1", OrderStatusEnum.DELIVERED, new BigDecimal(total),
				new BigDecimal(food), new BigDecimal(tax), new BigDecimal(platformFee), null, new BigDecimal(deliveryFee));
	}

	private BigDecimal amount(List<LedgerEntry> entries, LedgerAccountEnum account) {
		return entries.stream()
				.filter(entry -> entry.getLedgerAccount() == account)
				.findFirst().orElseThrow()
				.getAmount();
	}

	private BigDecimal sum(List<LedgerEntry> entries, LedgerTypeEnum type) {
		return entries.stream()
				.filter(entry -> entry.getLedgerType() == type)
				.map(LedgerEntry::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
