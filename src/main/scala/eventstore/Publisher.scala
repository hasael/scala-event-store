package eventstore

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import eventstore.dummy.RandomEventCreator
import eventstore.rabbitmq.{RabbitMqClient, RabbitPublisher}

object Publisher extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    IO {
      val QUEUE_NAME = ConfigFactory.load().getString("rabbit.payment.queue")
      val RABBIT_HOST = ConfigFactory.load().getString("rabbit.payment.host")
      val RABBIT_PORT = ConfigFactory.load().getInt("rabbit.payment.port")
      val RABBIT_USER = ConfigFactory.load().getString("rabbit.payment.username")
      val RABBIT_PASS = ConfigFactory.load().getString("rabbit.payment.password")
      val RABBIT_VHOST = ConfigFactory.load().getString("rabbit.vhost")
      val exchange = QUEUE_NAME

      val client = RabbitMqClient[IO](RABBIT_HOST, RABBIT_USER, RABBIT_PASS, RABBIT_VHOST, RABBIT_PORT)

      while (true) {
        println(s"publishing messages on $QUEUE_NAME")
        val message = RandomEventCreator().createRandomEvent()
        val task = client.publish(message, exchange, "")

        task.attempt.unsafeRunSync() match {
          case Left(error) => println(s"error publishing message $message. Error " + error.getMessage)
          case Right(_)    => println(s"sent message $message")
        }

        Thread.sleep(2000)
      }
      ExitCode.Success
    }
  }
}
