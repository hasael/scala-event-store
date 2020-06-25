package eventstore

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import eventstore.dummy.RandomEventCreator
import eventstore.rabbitmq.RabbitPublisher

object Publisher extends App {

  override def main(args: Array[String]) = {

    val QUEUE_NAME = ConfigFactory.load().getString("rabbit.payment.queue")
    val RABBIT_HOST = ConfigFactory.load().getString("rabbit.payment.host")
    val RABBIT_PORT = ConfigFactory.load().getInt("rabbit.payment.port")
    val RABBIT_USER = ConfigFactory.load().getString("rabbit.payment.username")
    val RABBIT_PASS = ConfigFactory.load().getString("rabbit.payment.password")
    val exchange = ""

    val rabbitPublisher = RabbitPublisher[IO](RABBIT_HOST, RABBIT_USER, RABBIT_PASS, RABBIT_PORT, exchange, QUEUE_NAME)

    rabbitPublisher.declareQueue()

    while (true) {
      println(s"publishing messages on $QUEUE_NAME")
      val message = RandomEventCreator().createRandomEvent()
      val task = rabbitPublisher.publish(message)

      task.attempt.unsafeRunSync() match {
        case Left(error) => println(s"error publishing message $message. Error " + error.getMessage)
        case Right(_)     => println(s"sent message $message")
      }
    
      Thread.sleep(2000)
    }
  }

}
