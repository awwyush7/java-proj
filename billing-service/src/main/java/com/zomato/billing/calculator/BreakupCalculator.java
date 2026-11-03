package com.zomato.billing.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zomato.billing.model.LedgerAccountEnum;
import com.zomato.billing.model.LedgerEntry;
import com.zomato.billing.model.LedgerTypeEnum;
import com.zomato.billing.model.OrderEvent;

@Component
public class BreakupCalculator {

	private final BigDecimal commissionPercent;
	private final BigDecimal pgCommissionPercent;

	public BreakupCalculator(@Value("${app.commission-percent}") BigDecimal commissionPercent,@Value("${app.pg-commission-percent}") BigDecimal pgCommissionPercent) {
		this.commissionPercent = commissionPercent;
		this.pgCommissionPercent = pgCommissionPercent;
	}

	// All amounts are in paise. The payment gateway keeps its fee and sends us the rest, so the
	// two debits (money received + gateway fee) add up to the same total as the credits.
	public List<LedgerEntry> calculate(OrderEvent order) {
		BigDecimal commission = order.foodValue().multiply(commissionPercent)
				.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
		BigDecimal pgCommission = order.totalAmount().multiply(pgCommissionPercent)
				.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
		BigDecimal received = order.totalAmount().subtract(pgCommission);
		BigDecimal payout = order.foodValue().subtract(commission);

		return List.of(
				entry(order, LedgerAccountEnum.TOTAL, received, LedgerTypeEnum.DEBIT),
				entry(order, LedgerAccountEnum.PAYMENT_GATEWAY_FEE, pgCommission, LedgerTypeEnum.DEBIT),
				entry(order, LedgerAccountEnum.TAX, order.tax(), LedgerTypeEnum.CREDIT),
				entry(order, LedgerAccountEnum.PLATFORM_FEE, order.platformFee(), LedgerTypeEnum.CREDIT),
				entry(order, LedgerAccountEnum.COMMISSION, commission, LedgerTypeEnum.CREDIT),
				entry(order, LedgerAccountEnum.RESTAURANT_PAYABLE, payout, LedgerTypeEnum.CREDIT),
				entry(order, LedgerAccountEnum.DELIVERY_PAYABLE, order.deliveryFee(), LedgerTypeEnum.CREDIT)
		);
	}

	private LedgerEntry entry(OrderEvent order, LedgerAccountEnum type, BigDecimal amount, LedgerTypeEnum ledgerType) {
		LedgerEntry entry = new LedgerEntry();
		entry.setOrderId(order.orderId());
		entry.setLedgerId("LEDGER#" + order.orderId() + "#" + type.name());
		entry.setOrderStatus(order.status());
		entry.setMerchantId(order.merchantId());
		entry.setServiceId(order.serviceId());
		entry.setLedgerAccount(type);
		entry.setLedgerType(ledgerType);
		entry.setAmount(amount);
		entry.setOrderTime(order.orderTime());
		return entry;
	}
}