package eventstore.events

import play.api.libs.json.Json

case class PaymentDeclined(transactionId: String, amount: Double, userId: String, currency: String, reason: String,
                           paymentData: PaymentData, transactionTime: String) extends PaymentEvent {
}

object PaymentDeclined {
  implicit val userJsonFormat = Json.format[PaymentDeclined]
}
