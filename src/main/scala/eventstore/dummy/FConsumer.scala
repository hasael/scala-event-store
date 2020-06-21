package eventstore.dummy

import cats.effect.{IO, Sync}
import eventstore.domain.{EventProcessor, MessageProcessor}
import eventstore.parsers.EventParser
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object FConsumer extends App {

  implicit def unsafeLogger[F[_] : Sync] = Slf4jLogger.getLogger[F]

  override def main(args: Array[String]): Unit = {
    val rabbitConsumer = new FQueueConsumer()
    rabbitConsumer.declareQueue()
    val messageProcessor = buildMessageProcessor()

    val onMessage = (message: String) => {
      for {
        _ <- messageProcessor.processMessage(message)
          .handleErrorWith(t => Logger[IO].error(t)("Error occurred"))
      } yield ()
      }.unsafeRunSync()

    val onCancel = (consumerTag: String) => {}

    rabbitConsumer.startConsumer(
      "",
      autoAck = true,
      onMessage,
      onCancel
    )

    while (true) {
      // we don't want to kill the receiver,
      // so we keep him alive waiting for more messages
      Thread.sleep(1000)
    }
  }

  private def buildMessageProcessor(): MessageProcessor[IO] = {
    val rabbitFraudPublisher = new FMessagePublisher[IO]()
    rabbitFraudPublisher.declareQueue()

    val sqlRepository = new FModelsRepository[IO]()
    val eventParser = EventParser()
    val eventsRepository = new FEventsRepository[IO]()

    val eventProcessor = new EventProcessor[IO](eventsRepository, sqlRepository, rabbitFraudPublisher)

    MessageProcessor(eventParser, eventProcessor)
  }

}
