# scala-event-store
A simple layer in scala that will consume events from a rabbitMQ Exchange and persist them to a CassandraDB, similarly to an event store.
Additionally, for certain events it will have additional behaviour like map and publish the message to a separate queue.
I will simulate a payment system CQRS, where wi will consume through rabbitMQ one of the following ecents in json

- PaymentAccepted
- PaymentDeclined
- PaymentPending

All these events will be persisted to the Repository.
PaymentDeclined will also be mapped to PaymentFailed event and sent to another rabbitMQ queue (hypothetically consumed by an anti-fraud system ecc) 
