# Billing Service

A Spring Boot service that writes the ledger for delivered orders. It reads each delivered order from Kafka, splits the money into ledger rows, and saves them to DynamoDB. Every order's debits equal its credits.

```
Go Order Service -> Kafka (orders.delivered) -> OrderConsumer -> BillingService -> BreakupCalculator
                                                                                -> LedgerRepository -> DynamoDB (billing_ledger)
```

## The event it reads

The Go Order Service publishes this when an order reaches `DELIVERED`. All amounts are in paise.

```json
{
  "order_id": "1fd639f4-5d59-5a8d-80d1-4479d9e566f1",
  "merchant_id": "M-1",
  "service_id": "S-1",
  "status": "DELIVERED",
  "total_amount": 127200,
  "food_value": 100000,
  "tax": 21200,
  "platform_fee": 1000,
  "order_time": "2026-11-02T13:45:03Z",
  "delivery_fee": 5000
}
```

The Go service works out the customer's bill, with a hard-coded platform fee of ₹10 and delivery fee of ₹50: `tax` is 20% of food value + platform fee + delivery fee, and `total_amount` adds the tax to those three. These field names must match `OrderEvent.java`.

Only `DELIVERED` orders are billed. Other values in `OrderStatusEnum` (`PROCESSED`, `ON_THE_WAY`, `CANCELLED`) are ignored, and any other status fails to parse.

## How the ledger is split

The payment gateway keeps its fee and sends us the rest, so the money received and the gateway fee are the debits. The credits show who the money belongs to.

Rates are in `application.yml`: `app.commission-percent` (20) and `app.pg-commission-percent` (10).

| Ledger account | Type | How it's calculated | Example |
|---|---|---|---|
| `TOTAL` | Debit | `total_amount` minus the gateway fee, which is what reaches our account | 114480 |
| `PAYMENT_GATEWAY_FEE` | Debit | 10% of `total_amount` | 12720 |
| `TAX` | Credit | `tax` from the event | 21200 |
| `PLATFORM_FEE` | Credit | `platform_fee` from the event | 1000 |
| `COMMISSION` | Credit | 20% of `food_value` | 20000 |
| `RESTAURANT_PAYABLE` | Credit | `food_value` minus commission | 80000 |
| `DELIVERY_PAYABLE` | Credit | `delivery_fee` from the event | 5000 |
| **Total** | | Debits and credits are equal | **127200** |

Each row's key is `orderId` (partition key) plus `ledgerId` (sort key), where `ledgerId` looks like `LEDGER#<order_id>#TAX`.

An order is saved in one DynamoDB transaction, so all seven rows are written or none are. A condition on each row means an order is never billed twice.

## Run

This service uses the Kafka and DynamoDB started by the Go project, so start that first:

```bash
cd ../go_services && make up COMPOSE="docker compose"
```

Then, from this folder:

```bash
cd billing-service && mvn spring-boot:run
```

It runs on port 8090. Move an order in the Go service to `DELIVERED`, then read its ledger:

```bash
curl localhost:8090/ledger/<order_id>
```

To bill an order without Kafka, save the JSON above as `order.json` and send it to `POST /bill`. It runs the same code as the Kafka consumer, so only `DELIVERED` orders are billed and a repeat is skipped:

```bash
curl -X POST localhost:8090/bill -H 'Content-Type: application/json' -d @order.json
```

## Test

```bash
cd billing-service && mvn test
```

## Known limits

- A message that keeps failing is tried up to 10 times and then skipped, so that order is not billed. A dead-letter topic would keep it.
- The service doesn't check the event's amounts. Debits only equal credits if `total_amount` is the sum of `food_value`, `tax`, `platform_fee` and `delivery_fee`, as the Go service sends it.
- DynamoDB can't change an existing table's keys. If `billing_ledger` was created with older keys, delete it before running. `make down` in the Go project does this, but deletes all its other data too.
