package eventstore

import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}
import eventstore.events.PaymentAccepted
import play.api.libs.json.Json

object Consumer {

  def main(args: Array[String]) = {
    val QUEUE_NAME = "hello"

    val factory = new ConnectionFactory()
    factory.setHost("localhost")

    val connection = factory.newConnection()
    val channel = connection.createChannel()

    channel.queueDeclare(QUEUE_NAME, false, false, false, null)

    println(s"waiting for messages on $QUEUE_NAME")

    val callback: DeliverCallback = (consumerTag, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"Received $message with tag $consumerTag")
      val jsonObject = Json.parse(message)
      val paymentAccepted : PaymentAccepted = jsonObject.as[PaymentAccepted]

      val trxId: String = paymentAccepted.transactionId
      println(s"$trxId")
    }

    val cancel: CancelCallback = consumerTag => {}

    val autoAck = true
    channel.basicConsume(QUEUE_NAME, autoAck, callback, cancel)

    while (true) {
      // we don't want to kill the receiver,
      // so we keep him alive waiting for more messages
      Thread.sleep(1000)
    }

    channel.close()
    connection.close()
  }
}
