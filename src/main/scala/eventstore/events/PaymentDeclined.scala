package eventstore.events

import play.api.libs.json.Json

case class PaymentDeclined(transactionId: String, amount: Double, userId: String, currency: String, reason: String,
                           paymentData: PaymentData, transactionTime: String) extends PaymentEvent {
  override def eventName: String = s"PaymentDeclined $transactionId"
}

object PaymentDeclined {
  implicit val userJsonFormat = Json.format[PaymentDeclined]
}
