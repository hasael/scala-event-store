package eventstore.events

case class PaymentFailed(eventType: String, transactionId: String, amount: Double, userId: String, currency: String,
                         reason: String, paymentType: String, panId: String, transactionTime: String)
