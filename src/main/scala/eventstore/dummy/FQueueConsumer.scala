package eventstore.dummy

import eventstore.domain.QueueConsumer
import java.{util => ju}
import scala.util.Random
import java.time.Instant
import java.time.ZoneId

class FQueueConsumer extends QueueConsumer {

  override def startConsumer[A](queueName: String, autoAck: Boolean, onMessage: String => A, onCancel: String => Unit) = {
      onMessage(randomPaymentAccepted())
      onMessage(randomPaymentAccepted())
  }

  override def declareQueue(): Unit = {}
  
    private def randomPaymentAccepted(): String = {
    val transactionId = ju.UUID.randomUUID().toString
    val amount = Random.nextInt(1500) + 1
    val userId = ju.UUID.randomUUID().toString
    val cardId = ju.UUID.randomUUID().toString
    val transactionTime = Instant.now().minusSeconds(Random.nextInt(1000000)).atZone(ZoneId.systemDefault()).toString
    val pspId = Random.nextInt(7)
    val currencies = Seq("USD", "EUR", "GBP", "AUD", "CAD")
    val paymentTypes = Seq("CREDIT CARD", "PAYPAL", "ALIPAY", "WIRE TRANSFER")
    val currency = currencies(Random.nextInt(5))
    val paymentType = paymentTypes(Random.nextInt(4))
    "{\n  \"eventType\":\"PaymentAccepted\",\n  \"transactionId\": \"" + transactionId + "\",\n  \"amount\": " + amount + ",\n  \"userId\" : \"" + userId + "\",\n  " +
      "\"currency\": \"" + currency + "\",\n  \"paymentData\": {\n    \"paymentType\": \"" + paymentType + "\",\n    \"pspId\": \"" + pspId + "\",\n    \"userAccountId\": 0,\n    " +
      "\"cardId\": \"" + cardId + "\"\n  },\n  \"transactionTime\": \"" + transactionTime + "\"\n}"
  }
}
