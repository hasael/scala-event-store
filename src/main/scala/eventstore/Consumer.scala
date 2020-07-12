package eventstore

import java.nio.file.Paths

import cats.effect.{ContextShift, IO, Sync}
import com.datastax.oss.driver.api.core.CqlSession
import com.typesafe.config.ConfigFactory
import eventstore.domain.{EventProcessor, MessageProcessor, PaymentMessage}
import eventstore.parsers.EventParser
import eventstore.rabbitmq.{RabbitMqClient, RabbitPublisher}
import eventstore.repositories.{CassandraRepository, SqlRepository}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

object Consumer extends App {
  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val QUEUE_NAME = ConfigFactory.load().getString("rabbit.payment.queue")
  val rabbitConsumer = buildRabbitConsumer()
  val messageProcessor = buildMessageProcessor()
  val onMessage = (message: PaymentMessage) =>
    for {
      _ <- messageProcessor
        .processMessage(message)
        .handleErrorWith(t => Logger[IO].error(t)("Error occurred"))
    } yield ()

  val task = rabbitConsumer.autoAckConsumer(QUEUE_NAME, onMessage)

  task
    .handleErrorWith(t => Logger[IO].error(t)("Fatal error occurred"))
    .unsafeRunSync()

  while (true) {
    // we don't want to kill the receiver,
    // so we keep him alive waiting for more messages
    Thread.sleep(1000)
  }

  private def buildRabbitConsumer(): RabbitMqClient[IO] = {
    //println(s"Creating consumer for messages on $queueName")
    val RABBIT_HOST = ConfigFactory.load().getString("rabbit.payment.host")
    val RABBIT_PORT = ConfigFactory.load().getInt("rabbit.payment.port")
    val RABBIT_USER = ConfigFactory.load().getString("rabbit.payment.username")
    val RABBIT_PASS = ConfigFactory.load().getString("rabbit.payment.password")
    val RABBIT_VHOST = ConfigFactory.load().getString("rabbit.vhost")
    RabbitMqClient[IO](RABBIT_HOST, RABBIT_USER, RABBIT_PASS, RABBIT_VHOST, RABBIT_PORT)
  }

  private def buildMessageProcessor() = {

    val FRAUD_QUEUE_NAME = ConfigFactory.load().getString("rabbit.antifraud.queue")
    val FRAUD_RABBIT_HOST = ConfigFactory.load().getString("rabbit.antifraud.host")
    val FRAUD_RABBIT_PORT = ConfigFactory.load().getInt("rabbit.antifraud.port")
    val FRAUD_RABBIT_USER = ConfigFactory.load().getString("rabbit.antifraud.username")
    val FRAUD_RABBIT_PASS = ConfigFactory.load().getString("rabbit.antifraud.password")
    val RABBIT_VHOST = ConfigFactory.load().getString("rabbit.vhost")

    val MYSQL_MODELS_HOST = ConfigFactory.load().getString("mysql.models.host")
    val MYSQL_MODELS_PORT = ConfigFactory.load().getInt("mysql.models.port")
    val MYSQL_MODELS_USER = ConfigFactory.load().getString("mysql.models.username")
    val MYSQL_MODELS_PASSWORD = ConfigFactory.load().getString("mysql.models.password")
    val MYSQL_MODELS_SCHEMA = ConfigFactory.load().getString("mysql.models.schema")

    val eventsUsername = ConfigFactory.load().getString("events.username")
    val eventsPassword = ConfigFactory.load().getString("events.password")
    val cloudSecurePath = ConfigFactory.load().getString("cassandra.secureConnectionPath")
    val eventsKeyspace = ConfigFactory.load().getString("events.keyspace")

    val client = RabbitMqClient[IO](FRAUD_RABBIT_HOST, FRAUD_RABBIT_USER, FRAUD_RABBIT_PASS, RABBIT_VHOST, FRAUD_RABBIT_PORT)
    val rabbitFraudPublisher =
      RabbitPublisher[IO](client, FRAUD_QUEUE_NAME, "")

    val session = CqlSession
      .builder()
      .withCloudSecureConnectBundle(Paths.get(cloudSecurePath))
      .withAuthCredentials(eventsUsername, eventsPassword)
      .withKeyspace(eventsKeyspace)
      .build()

    val sqlRepository = new SqlRepository[IO](MYSQL_MODELS_HOST, MYSQL_MODELS_PORT, MYSQL_MODELS_SCHEMA, MYSQL_MODELS_USER, MYSQL_MODELS_PASSWORD)
    val cassandraRepository = new CassandraRepository[IO](session)
    val eventProcessor = new EventProcessor[IO](cassandraRepository, sqlRepository, rabbitFraudPublisher)

    val eventParser = EventParser()

    MessageProcessor(eventParser, eventProcessor)
  }
}
