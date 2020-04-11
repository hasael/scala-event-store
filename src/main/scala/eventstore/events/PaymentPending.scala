package eventstore.events

import play.api.libs.json.Json

case class PaymentPending(transactionId: String, amount: Double, userId: String, currency: String,
                          paymentType: String, transactionTime: String) extends PaymentEvent {
}
object PaymentPending {
  implicit val userJsonFormat = Json.format[PaymentPending]
}