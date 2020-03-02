# scala-event-store
A simple layer in scala that will consume events from a rabbitMQ Exchange and persist them to a CassandraDB, similarly to an event store.
Additionally, for certain events it will have additional behaviour like map and publish the message to a separate queue.
I will simulate a payment system CQRS, where wi will consume through rabbitMQ one of the following ecents in json

- PaymentAccepted
```json
{
  "transactionId": "guid",
  "amount": 3.2,
  "currency": "",
  "paymentData": {
    "paymentType": "",
    "pspId": "",
    "userAccountId": 0,
    "cardId": "guid"
  },
  "transactionTime": "2020-03-02T18:12:17.523Z"
}
```
- PaymentDeclined
```json
{
  "transactionId": "guid",
  "amount": 3.2,
  "currency": "",
  "reason" : "",
  "paymentData": {
    "paymentType": "",
    "pspId": "",
    "userAccountId": 0,
    "cardId": "guid"
  },
  "transactionTime": "2020-03-02T18:12:17.523Z"
}
```
- PaymentPending
```json
{
  "transactionId": "guid",
  "amount": 3.2,
  "currency": "",
  "paymentType" : "",
  "transactionTime": "2020-03-02T18:12:17.523Z"
}
```
All these events will be persisted to the Repository.
PaymentDeclined will also be mapped to PaymentFailed event and sent to another rabbitMQ queue (hypothetically consumed by an anti-fraud system ecc) 
