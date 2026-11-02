package com.zomato.billing.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zomato.billing.model.LedgerEntry;
import com.zomato.billing.model.OrderEvent;

@Component
public class BreakupCalculator {

	private final BigDecimal commissionPercent;

	public BreakupCalculator(@Value("${app.commission-percent}") BigDecimal commissionPercent) {
		this.commissionPercent = commissionPercent;
	}

	// Example: itemTotal 100 at 20% gives commission 20 and merchant payout 80.
	public List<LedgerEntry> calculate(OrderEvent order) {
		BigDecimal commission = order.itemTotal().multiply(commissionPercent)
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		BigDecimal payout = order.itemTotal().subtract(commission);

		return List.of(
				entry(order, "COMMISSION", commission),
				entry(order, "DELIVERY_FEE", order.deliveryFee()),
				entry(order, "MERCHANT_PAYOUT", payout));
	}

	private LedgerEntry entry(OrderEvent order, String type, BigDecimal amount) {
		LedgerEntry entry = new LedgerEntry();
		entry.setOrderId(order.orderId());
		entry.setEntryType(type);
		entry.setMerchantId(order.merchantId());
		entry.setServiceId(order.serviceId());
		entry.setAmount(amount);
		return entry;
	}
}
