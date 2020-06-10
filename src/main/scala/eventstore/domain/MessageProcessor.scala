package eventstore.domain

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import eventstore.parsers.EventParser
import scala.concurrent.Future
import eventstore.context.Syntax._
import eventstore.context.FutureContext._
import cats.instances.future._
import cats.instances.list._
import scala.util.Success
import scala.util.Failure

class MessageProcessor(eventParser: EventParser, eventProcessor: EventProcessor) {
  def processMessage(message: String): Future[(List[String], Unit)] = {

    val paymentEvent = for {
      event <- Future.fromTry(EventParser().parseEvent(message)).asLogged
      result <- eventProcessor.processEvent(event)
    } yield result

    val task = paymentEvent.run

    task.onComplete {
      case Success((a, _)) =>
        println(s"Received $message")
        a.foreach(println)
        println("Correctly parsed event.")

      case Failure(exception) => println("Error: " + exception.getMessage)
    }
    task
  }
}

object MessageProcessor {
  def apply(eventParser: EventParser, eventProcessor: EventProcessor): MessageProcessor = {
    new MessageProcessor(eventParser, eventProcessor)
  }
}
