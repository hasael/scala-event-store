package eventstore.domain

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import eventstore.parsers.EventParser
import scala.concurrent.Future
import eventstore.context.Syntax._
import cats.instances.future._
import cats.instances.list._
import cats.effect.Sync
import cats.implicits._

class MessageProcessor[F[_]: Sync](eventParser: EventParser, eventProcessor: EventProcessor[F]) {
  def processMessage(message: String): F[Unit] = {

    for {
      event <- Sync[F].fromTry(eventParser.parseEvent(message))
      result <- eventProcessor.processEvent(event)
    } yield result

    // val task = paymentEvent.run
    /*val a = Sync[F].attempt(paymentEvent)
    task.attempt.unsafeRunSync {
      case Success((a, _)) =>
        println(s"Received $message")
        a.foreach(println)
        println("Correctly parsed event.")

      case Failure(exception) => println("Error: " + exception.getMessage)
    }
   task*/
  }
}

object MessageProcessor {
  def apply[F[_]: Sync](eventParser: EventParser, eventProcessor: EventProcessor[F]): MessageProcessor[F] = {
    new MessageProcessor[F](eventParser, eventProcessor)
  }
}
