package eventstore.dummy

import cats.effect.IO._
import cats.effect.{IO, Sync, _}
import eventstore.domain.{EventProcessor, MessageProcessor, PaymentMessage}
import eventstore.parsers.EventParser
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import fs2.Stream

import scala.concurrent.ExecutionContext

object FConsumer extends App {
  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)

  val rabbitConsumer = new FQueueClient()
  val messageProcessor = buildMessageProcessor()

  val onMessage = (message: PaymentMessage) =>
    {
      for {
        _ <- messageProcessor
          .processMessage(message)
          .handleErrorWith(t => Logger[IO].error(t)("Error occurred"))
      } yield ()
    }.unsafeRunSync()

  val task = rabbitConsumer.autoAckConsumer("QUEUE_NAME", onMessage)

  while (true) {
    // we don't want to kill the receiver,
    // so we keep him alive waiting for more messages
    Thread.sleep(1000)
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
