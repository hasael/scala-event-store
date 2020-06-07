package eventstore

import cats.instances.future._
import cats.instances.list._
import com.typesafe.config.ConfigFactory
import eventstore.context.FutureContext._
import eventstore.context.Syntax._
import eventstore.domain.EventProcessor
import eventstore.parsers.EventParser
import eventstore.rabbitmq.{RabbitConsumer, RabbitPublisher}
import eventstore.repositories.{CassandraRepository, SqlRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Consumer {

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

    val MYSQL_MODELS_HOST = ConfigFactory.load().getString("mysql.models.host")
    val MYSQL_MODELS_PORT = ConfigFactory.load().getInt("mysql.models.port")
    val MYSQL_MODELS_USER = ConfigFactory.load().getString("mysql.models.username")
    val MYSQL_MODELS_PASSWORD = ConfigFactory.load().getString("mysql.models.password")
    val MYSQL_MODELS_SCHEMA = ConfigFactory.load().getString("mysql.models.schema")

    val rabbitFraudPublisher = RabbitPublisher(FRAUD_RABBIT_HOST, FRAUD_RABBIT_USER, FRAUD_RABBIT_PASS, FRAUD_RABBIT_PORT, "", FRAUD_QUEUE_NAME)
    rabbitFraudPublisher.declareQueue()

    val rabbitConsumer = RabbitConsumer(RABBIT_HOST, RABBIT_USER, RABBIT_PASS, RABBIT_PORT, "", QUEUE_NAME)
    rabbitConsumer.declareQueue()

    val sqlRepository = new SqlRepository(MYSQL_MODELS_HOST, MYSQL_MODELS_PORT, MYSQL_MODELS_SCHEMA, MYSQL_MODELS_USER, MYSQL_MODELS_PASSWORD)

    val eventProcessor = new EventProcessor(new CassandraRepository(), sqlRepository, rabbitFraudPublisher)

    println(s"Creating consumer for messages on $QUEUE_NAME")

    val onMessage = (message: String) => {

      val paymentEvent = for {
        event <- Future.fromTry(EventParser.parseEvent(message)).asLogged
        result <- eventProcessor.processEvent(event)
      } yield result

      val task = paymentEvent.run
      task.onComplete {
        case Success((a, _)) =>
          println(s"Received $message")
          a.foreach(println)
          println("Correctly parsed event.")

        case Failure(exception) => println("Error: " + exception.getMessage)
      }
      Await.result(task, Duration.Inf)
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
