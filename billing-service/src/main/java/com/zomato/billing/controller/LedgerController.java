package com.zomato.billing.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.zomato.billing.model.LedgerEntry;
import com.zomato.billing.repository.LedgerRepository;

@RestController
public class LedgerController {

	private final LedgerRepository repository;

	public LedgerController(LedgerRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/ledger/{orderId}")
	public List<LedgerEntry> getLedger(@PathVariable String orderId) {
		return repository.findByOrderId(orderId);
	}
}
