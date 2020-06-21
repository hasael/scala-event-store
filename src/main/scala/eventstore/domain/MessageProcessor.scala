package eventstore.domain

import cats.effect.Sync
import cats.implicits._
import eventstore.parsers.EventParser
import io.chrisdavenport.log4cats.Logger

class MessageProcessor[F[_] : Sync : Logger](eventParser: EventParser, eventProcessor: EventProcessor[F]) {
  def processMessage(message: String): F[Unit] = {

    for {
      _ <- Logger[F].info(s"Received $message")
      event <- Sync[F].fromTry(eventParser.parseEvent(message))
      _<- Logger[F].info(s"Processing event as "+ event.getClass.toString)
      result <- eventProcessor.processEvent(event)
    } yield result

  }
}

object MessageProcessor {
  def apply[F[_] : Sync : Logger](eventParser: EventParser, eventProcessor: EventProcessor[F]): MessageProcessor[F] = {
    new MessageProcessor[F](eventParser, eventProcessor)
  }
}
