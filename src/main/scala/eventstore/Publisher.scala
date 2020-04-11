package eventstore

import java.time.{Instant, ZoneId}
import java.util.UUID

import com.rabbitmq.client.ConnectionFactory
import com.typesafe.config.ConfigFactory

import scala.util.Random

object Publisher {

  def main(args: Array[String]) = {


    val QUEUE_NAME = ConfigFactory.load().getString("rabbit.payment.queue")
    val RABBIT_HOST = ConfigFactory.load().getString("rabbit.payment.host")
    val RABBIT_PORT = ConfigFactory.load().getInt("rabbit.payment.port")
    val RABBIT_USER = ConfigFactory.load().getString("rabbit.payment.username")
    val RABBIT_PASS = ConfigFactory.load().getString("rabbit.payment.password")

    val factory = new ConnectionFactory()
    factory.setHost(RABBIT_HOST)
    factory.setPort(RABBIT_PORT)
    factory.setUsername(RABBIT_USER)
    factory.setPassword(RABBIT_PASS)

    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(QUEUE_NAME, false, false, false, null)

    while (true) {
      println(s"publishing messages on $QUEUE_NAME")
      val message = getRandomEvent()
      val exchange = ""
      channel.basicPublish(exchange, QUEUE_NAME, null, message.getBytes)
      println(s"sent message $message")

      Thread.sleep(2000)
    }
    channel.close()
    connection.close()
  }

  private def getRandomEvent(): String = {
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
