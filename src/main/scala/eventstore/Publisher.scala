package eventstore

import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import eventstore.config.RabbitConfig
import eventstore.dummy.RandomEventCreator
import eventstore.rabbitmq.RabbitMqClient
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._
object Publisher extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- loadConfig()
      task <- startPublisher(config)
    } yield task
  }
  private def startPublisher(rabbitConfig: RabbitConfig) = {
    IO {
      val client = RabbitMqClient[IO](rabbitConfig.host, rabbitConfig.username, rabbitConfig.password, rabbitConfig.vhost, rabbitConfig.port)

      while (true) {
        println(s"publishing messages on ${rabbitConfig.queue}")
        val message = RandomEventCreator().createRandomEvent()
        val task = client.publish(message, rabbitConfig.queue, "")

        task.attempt.unsafeRunSync() match {
          case Left(error) => println(s"error publishing message $message. Error " + error.getMessage)
          case Right(_)    => println(s"sent message $message")
        }

        Thread.sleep(2000)
      }
      ExitCode.Success
    }.handleErrorWith(t => IO(println(s"Fatal error ${t.getMessage}")) >> IO(ExitCode.Error))
  }
  private def loadConfig(): IO[RabbitConfig] = {
    val rabbitPaymentConfig = ConfigSource.default.at("rabbit-payment").load[RabbitConfig]

    IO.fromEither(rabbitPaymentConfig.leftMap(c => new Throwable(s"Config error ${c.prettyPrint()}")))
  }
}
