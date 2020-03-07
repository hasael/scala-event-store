package eventstore.events

case class PaymentFailed(transactionId: String, amount: Double, userId: String, currency: String,
                         paymentData: PaymentData, transactionTime: String)

case class PaymentData(paymentType: String, pspId: String, userAccoundId: String, cardId: String)