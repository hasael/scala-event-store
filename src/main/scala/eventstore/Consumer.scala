package eventstore

import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}
import eventstore.domain.EventProcessor
import eventstore.parsers.EventParser
import eventstore.repositories.CassandraRepository

import scala.util.{Failure, Success, Try}
import com.typesafe.config.ConfigFactory

object Consumer {

  def main(args: Array[String]) = {
    val eventProcessor = new EventProcessor(new CassandraRepository())

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

    println(s"waiting for messages on $QUEUE_NAME")

    val callback: DeliverCallback = (consumerTag, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"Received $message with tag $consumerTag")

      val printMessage = for {
        paymentEvent <- EventParser.parseEvent(message)
        _ <- eventProcessor.processEvent(paymentEvent)
        printMessage <- Try("Correctly parsed event ")
      } yield printMessage

      printMessage match {
        case Success(value) => println(value)
        case Failure(exception) => println("Error: " + exception.getMessage)
      }
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
