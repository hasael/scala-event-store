package eventstore.dummy

import scala.util.Random
import java.util.UUID
import java.time.Instant
import java.time.ZoneId

class RandomEventCreator {

  def createRandomEvent(): String = {
    val randInt = Random.nextInt(3)
    if (randInt == 0)
      randomPaymentAccepted()
    else if (randInt == 1)
      randomPaymentDeclined()
    else
      randomPaymentPending()
  }

  private def randomPaymentAccepted(): String = {
    val transactionId = UUID.randomUUID().toString
    val amount = Random.nextInt(1500) + 1
    val userId = UUID.randomUUID().toString
    val cardId = UUID.randomUUID().toString
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

  private def randomPaymentDeclined(): String = {
    val transactionId = UUID.randomUUID().toString
    val amount = Random.nextInt(1500) + 1
    val userId = UUID.randomUUID().toString
    val cardId = UUID.randomUUID().toString
    val transactionTime = Instant.now().minusSeconds(Random.nextInt(1000000)).atZone(ZoneId.systemDefault()).toString
    val pspId = Random.nextInt(7)
    val currencies = Seq("USD", "EUR", "GBP", "AUD", "CAD")
    val paymentTypes = Seq("CREDIT CARD", "PAYPAL", "ALIPAY", "WIRE TRANSFER")
    val currency = currencies(Random.nextInt(5))
    val paymentType = paymentTypes(Random.nextInt(4))
    "{\n  \"eventType\":\"PaymentDeclined\",\n  \"transactionId\": \"" + transactionId + "\",\n  " +
      "\"userId\" : \"" + userId + "\",\n  \"amount\": " + amount + ",\n  \"currency\": \"" + currency + "\",\n  \"reason\" : \"\",\n  " +
      "\"paymentData\": {\n    \"paymentType\": \"" + paymentType + "\",\n    \"pspId\": \"" + pspId + "\",\n    " +
      "\"userAccountId\": 0,\n    \"cardId\": \"" + cardId + "\"\n  },\n  " +
      "\"transactionTime\": \"" + transactionTime + "\"\n}"
  }

  private def randomPaymentPending(): String = {
    val transactionId = UUID.randomUUID().toString
    val userId = UUID.randomUUID().toString
    val transactionTime = Instant.now().minusSeconds(Random.nextInt(1000000)).atZone(ZoneId.systemDefault()).toString
    val currencies = Seq("USD", "EUR", "GBP", "AUD", "CAD")
    val paymentTypes = Seq("CREDIT CARD", "PAYPAL", "ALIPAY", "WIRE TRANSFER")
    val currency = currencies(Random.nextInt(5))
    val paymentType = paymentTypes(Random.nextInt(4))
    "{\n  \"eventType\":\"PaymentPending\",\n  \"transactionId\": \"" + transactionId + "\",\n  " +
      "\"userId\" : \"" + userId + "\",\n  \"amount\": 3.2,\n  \"currency\": \"" + currency + "\",\n  \"paymentType\" : \"" + paymentType + "\",\n  " +
      "\"transactionTime\": \"" + transactionTime + "\"\n}"
  }
}

object RandomEventCreator {
  def apply() = new RandomEventCreator()
}
