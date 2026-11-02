# Billing Service

Reads delivered orders from Kafka, calculates the commission and merchant payout, and saves them as ledger rows in DynamoDB.

```
Kafka (order-events) -> OrderConsumer -> BillingService -> BreakupCalculator
                                                        -> LedgerRepository -> DynamoDB
```

The commission rate is one fixed value in `application.yml` (`app.commission-percent`).

## Run

```bash
docker compose up -d
cd billing-service && mvn spring-boot:run
```

In another terminal, from this folder:

```bash
./order-producer/send-event.sh
curl localhost:8080/ledger/ORD-1
```

For an order of 100 with a delivery fee of 30, the ledger has commission 20, delivery fee 30 and merchant payout 80.
Sending the same order again does not bill it twice.

## Test

```bash
cd billing-service && mvn test
```
