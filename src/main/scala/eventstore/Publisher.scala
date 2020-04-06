package eventstore

import java.util.UUID

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import com.rabbitmq.client.ConnectionFactory

import scala.util.Random

object Publisher {

  def main(args: Array[String]) = {

    val QUEUE_NAME = "hello"
    val factory = new ConnectionFactory()
    factory.setHost("localhost")

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
    "{\n  \"eventType\":\"PaymentAccepted\",\n  \"transactionId\": \"" + transactionId + "\",\n  \"amount\": 3.2,\n  \"userId\" : \"guid\",\n  \"currency\": \"\",\n  \"paymentData\": {\n    \"paymentType\": \"\",\n    \"pspId\": \"\",\n    \"userAccountId\": 0,\n    \"cardId\": \"guid\"\n  },\n  \"transactionTime\": \"2020-03-02T18:12:17.523Z\"\n}"
  }

  private def randomPaymentDeclined(): String = {
    val transactionId = UUID.randomUUID().toString
    "{\n  \"eventType\":\"PaymentDeclined\",\n  \"transactionId\": \"" + transactionId + "\",\n  \"userId\" : \"guid\",\n  \"amount\": 3.2,\n  \"currency\": \"\",\n  \"reason\" : \"\",\n  \"paymentData\": {\n    \"paymentType\": \"\",\n    \"pspId\": \"\",\n    \"userAccountId\": 0,\n    \"cardId\": \"guid\"\n  },\n  \"transactionTime\": \"2020-03-02T18:12:17.523Z\"\n}"
  }

  private def randomPaymentPending(): String = {
    val transactionId = UUID.randomUUID().toString
    "{\n  \"eventType\":\"PaymentPending\",\n  \"transactionId\": \"" + transactionId + "\",\n  \"userId\" : \"guid\",\n  \"amount\": 3.2,\n  \"currency\": \"\",\n  \"paymentType\" : \"\",\n  \"transactionTime\": \"2020-03-02T18:12:17.523Z\"\n}"
  }

}
