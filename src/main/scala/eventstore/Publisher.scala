package eventstore

import java.time.{Instant, ZoneId}
import java.util.UUID

import com.typesafe.config.ConfigFactory
import eventstore.context.FutureContext._
import eventstore.rabbitmq.RabbitPublisher

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Random, Success}
import eventstore.dummy.RandomEventCreator
object Publisher extends App {

  override def main(args: Array[String]) = {

    val QUEUE_NAME = ConfigFactory.load().getString("rabbit.payment.queue")
    val RABBIT_HOST = ConfigFactory.load().getString("rabbit.payment.host")
    val RABBIT_PORT = ConfigFactory.load().getInt("rabbit.payment.port")
    val RABBIT_USER = ConfigFactory.load().getString("rabbit.payment.username")
    val RABBIT_PASS = ConfigFactory.load().getString("rabbit.payment.password")
    val exchange = ""

    val rabbitPublisher = RabbitPublisher(RABBIT_HOST, RABBIT_USER, RABBIT_PASS, RABBIT_PORT, exchange, QUEUE_NAME)

    rabbitPublisher.declareQueue()

    while (true) {
      println(s"publishing messages on $QUEUE_NAME")
      val message = RandomEventCreator().createRandomEvent()
      val task = rabbitPublisher.publish(message)
      task.onComplete {
        case Failure(error) => println(s"error publishing message $message. Error " + error.getMessage)
        case Success(_) => println(s"sent message $message")
      }
      Await.result(task, Duration.Inf)
      Thread.sleep(2000)
    }
  }

}
