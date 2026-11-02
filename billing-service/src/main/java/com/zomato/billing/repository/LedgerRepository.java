package com.zomato.billing.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zomato.billing.model.LedgerEntry;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

@Repository
public class LedgerRepository {

	private final DynamoDbEnhancedClient client;
	private final DynamoDbTable<LedgerEntry> table;

	public LedgerRepository(DynamoDbEnhancedClient client, DynamoDbTable<LedgerEntry> table) {
		this.client = client;
		this.table = table;
	}

	// One transaction: all rows are saved or none. The condition stops the same order being billed twice.
	public boolean saveIfNew(List<LedgerEntry> entries) {
		Expression isNew = Expression.builder().expression("attribute_not_exists(orderId)").build();
		var request = TransactWriteItemsEnhancedRequest.builder();
		for (LedgerEntry entry : entries) {
			request.addPutItem(table, TransactPutItemEnhancedRequest.builder(LedgerEntry.class)
					.item(entry).conditionExpression(isNew).build());
		}
		try {
			client.transactWriteItems(request.build());
			return true;
		} catch (TransactionCanceledException e) {
			boolean alreadyBilled = e.cancellationReasons().stream()
					.anyMatch(reason -> "ConditionalCheckFailed".equals(reason.code()));
			if (!alreadyBilled) {
				throw e;
			}
			return false;
		}
	}

	public List<LedgerEntry> findByOrderId(String orderId) {
		Key key = Key.builder().partitionValue(orderId).build();
		return table.query(QueryConditional.keyEqualTo(key)).items().stream().toList();
	}
}
