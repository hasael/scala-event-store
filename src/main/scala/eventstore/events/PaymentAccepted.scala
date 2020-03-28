package eventstore.events

import play.api.libs.json.Json

case class PaymentData(paymentType: String, pspId: String, userAccountId: Int, cardId: String)

object PaymentData {
  implicit val userJsonFormat = Json.format[PaymentData]
}

case class PaymentAccepted(transactionId: String, amount: Double, userId: String, currency: String,
                           paymentData: PaymentData, transactionTime: String) extends PaymentEvent {

  override def eventName: String = s"PaymentAccepted: $transactionId"
}
object PaymentAccepted {
  implicit val userJsonFormat = Json.format[PaymentAccepted]
}