package eventstore

import java.nio.file.Paths
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp, Sync}
import com.datastax.oss.driver.api.core.CqlSession
import eventstore.config.{CassandraConfig, Config, MySqlConfig, RabbitConfig}
import eventstore.domain.{EventProcessor, MessageProcessor, PaymentMessage}
import eventstore.parsers.EventParser
import eventstore.rabbitmq.{RabbitMqClient, RabbitPublisher}
import eventstore.repositories.{CassandraRepository, SqlRepository}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Consumer extends IOApp {
  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def run(args: List[String]): IO[ExitCode] = {
    val run = for {
      config <- loadConfig()
      task <- startConsumer(config) >> IO(ExitCode.Success)
    } yield task
    run.handleErrorWith(t => Logger[IO].error(t)("Fatal error occurred") >> IO(ExitCode.Error))
  }
  private def startConsumer(config: Config): IO[Unit] = {
    val rabbitConsumer = buildRabbitConsumer(config.rabbitPaymentConfig)
    val messageProcessor = buildMessageProcessor(config.rabbitFraundConfig, config.mySqlConfig, config.cassandraConfig)
    val onMessage = (message: PaymentMessage) =>
      for {
        _ <- messageProcessor
          .processMessage(message)
          .handleErrorWith(t => Logger[IO].error(t)("Error occurred"))
      } yield ()

    rabbitConsumer
      .autoAckConsumer(config.rabbitPaymentConfig.queue, onMessage)
  }

  private def loadConfig(): IO[Config] = {
    val config = for {
      rabbitPaymentConfig <- ConfigSource.default.at("rabbit-payment").load[RabbitConfig]
      rabbitFraudConfig <- ConfigSource.default.at("rabbit-antifraud").load[RabbitConfig]
      mySqlConfig <- ConfigSource.default.at("mysql-models").load[MySqlConfig]
      cassandraConfig <- ConfigSource.default.at("cassandra").load[CassandraConfig]
    } yield (Config(rabbitPaymentConfig, rabbitFraudConfig, mySqlConfig, cassandraConfig))

    IO.fromEither(config.leftMap(c => new Throwable(s"Config error ${c.prettyPrint()}")))
  }

  private def buildRabbitConsumer(rabbitConfig: RabbitConfig): RabbitMqClient[IO] = {
    //println(s"Creating consumer for messages on $queueName")
    RabbitMqClient[IO](rabbitConfig.host, rabbitConfig.username, rabbitConfig.password, rabbitConfig.vhost, rabbitConfig.port)
  }

  private def buildMessageProcessor(rabbitFraudConfig: RabbitConfig, mySqlConfig: MySqlConfig, cassandraConfig: CassandraConfig) = {

    val client = RabbitMqClient[IO](
      rabbitFraudConfig.host,
      rabbitFraudConfig.username,
      rabbitFraudConfig.password,
      rabbitFraudConfig.vhost,
      rabbitFraudConfig.port
    )
    val rabbitFraudPublisher =
      RabbitPublisher[IO](client, rabbitFraudConfig.queue, "")

    val session = CqlSession
      .builder()
      .withCloudSecureConnectBundle(Paths.get(cassandraConfig.secureConnectionPath))
      .withAuthCredentials(cassandraConfig.username, cassandraConfig.password)
      .withKeyspace(cassandraConfig.keyspace)
      .build()

    val sqlRepository = new SqlRepository[IO](mySqlConfig.host, mySqlConfig.port, mySqlConfig.schema, mySqlConfig.username, mySqlConfig.password)
    val cassandraRepository = new CassandraRepository[IO](session)
    val eventProcessor = new EventProcessor[IO](cassandraRepository, sqlRepository, rabbitFraudPublisher)

    val eventParser = EventParser()

    MessageProcessor(eventParser, eventProcessor)
  }

}
