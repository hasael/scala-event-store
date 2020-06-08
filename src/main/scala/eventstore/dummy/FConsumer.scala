package eventstore.dummy

import eventstore.parsers.EventParser
import eventstore.context.Syntax._
import eventstore.context.FutureContext._
import cats.syntax.all._
import cats.instances.future._
import cats.instances.list._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import eventstore.domain.EventProcessor

object FConsumer extends App {
  override def main(args: Array[String]): Unit = {
    val rabbitFraudPublisher = new FMessagePublisher()
    rabbitFraudPublisher.declareQueue()

    val rabbitConsumer = new FQueueConsumer()
    rabbitConsumer.declareQueue()

    val sqlRepository = new FModelsRepository()

    val eventsRepository = new FEventsRepository()

    val eventProcessor =
      new EventProcessor(eventsRepository, sqlRepository, rabbitFraudPublisher)
    val onMessage = (message: String) => {

      val paymentEvent = for {
        event <- Future.fromTry(EventParser.parseEvent(message)).asLogged
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
      Await.result(task, Duration.Inf)
    }

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
  
}
