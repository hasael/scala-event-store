package eventstore.events

import play.api.libs.json.Json

case class PaymentFailed(transactionId: String, amount: Double, userId: String, currency: String,
                         reason: String, paymentType: String, panId: String, transactionTime: String)
object PaymentFailed {
  implicit val userJsonFormat = Json.format[PaymentFailed]
}