package eventstore

import com.typesafe.config.ConfigFactory
import eventstore.domain.EventProcessor
import eventstore.parsers.EventParser
import eventstore.rabbitmq.{RabbitConsumer, RabbitPublisher}
import eventstore.repositories.CassandraRepository

import scala.util.{Failure, Success}

object Consumer  {

   def main(args: Array[String]) = {

    val QUEUE_NAME = ConfigFactory.load().getString("rabbit.payment.queue")
    val RABBIT_HOST = ConfigFactory.load().getString("rabbit.payment.host")
    val RABBIT_PORT = ConfigFactory.load().getInt("rabbit.payment.port")
    val RABBIT_USER = ConfigFactory.load().getString("rabbit.payment.username")
    val RABBIT_PASS = ConfigFactory.load().getString("rabbit.payment.password")

    val FRAUD_QUEUE_NAME = ConfigFactory.load().getString("rabbit.antifraud.queue")
    val FRAUD_RABBIT_HOST = ConfigFactory.load().getString("rabbit.antifraud.host")
    val FRAUD_RABBIT_PORT = ConfigFactory.load().getInt("rabbit.antifraud.port")
    val FRAUD_RABBIT_USER = ConfigFactory.load().getString("rabbit.antifraud.username")
    val FRAUD_RABBIT_PASS = ConfigFactory.load().getString("rabbit.antifraud.password")

    val rabbitFraudPublisher = RabbitPublisher(FRAUD_RABBIT_HOST, FRAUD_RABBIT_USER, FRAUD_RABBIT_PASS, FRAUD_RABBIT_PORT, "", FRAUD_QUEUE_NAME)
    rabbitFraudPublisher.declareQueue()

    val rabbitConsumer = RabbitConsumer(RABBIT_HOST, RABBIT_USER, RABBIT_PASS, RABBIT_PORT, "", QUEUE_NAME)
    rabbitConsumer.declareQueue()

    val eventProcessor = new EventProcessor(new CassandraRepository(), rabbitFraudPublisher)

    println(s"Creating consumer for messages on $QUEUE_NAME")

    val onMessage = (message: String) => {
      val paymentEvent = for {
        paymentEvent <- EventParser.parseEvent(message)
        _ <- eventProcessor.processEvent(paymentEvent)
      } yield paymentEvent

      paymentEvent match {
        case Success(_) => println("Correctly parsed event")
        case Failure(exception) => println("Error: " + exception.getMessage)
      }
    }

    val onCancel = (consumerTag: String) => {}

    rabbitConsumer.startConsumer(QUEUE_NAME, autoAck = true, onMessage, onCancel)

    while (true) {
      // we don't want to kill the receiver,
      // so we keep him alive waiting for more messages
      Thread.sleep(1000)
    }
    rabbitConsumer.onClose()
  }
}
