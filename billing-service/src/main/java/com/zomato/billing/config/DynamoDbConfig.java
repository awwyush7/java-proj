package com.zomato.billing.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zomato.billing.model.LedgerEntry;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@Configuration
public class DynamoDbConfig {

	@Bean
	public DynamoDbEnhancedClient dynamoDbClient(@Value("${app.dynamodb-endpoint}") String endpoint) {
		DynamoDbClient client = DynamoDbClient.builder()
				.endpointOverride(URI.create(endpoint))
				.region(Region.AP_SOUTH_1)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
				.build();
		return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
	}

	@Bean
	public DynamoDbTable<LedgerEntry> ledgerTable(DynamoDbEnhancedClient client) {
		DynamoDbTable<LedgerEntry> table = client.table("billing_ledger", TableSchema.fromBean(LedgerEntry.class));
		try {
			table.createTable();
		} catch (ResourceInUseException alreadyExists) {
			// nothing to do
		}
		return table;
	}
}
