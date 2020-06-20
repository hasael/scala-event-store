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
import eventstore.domain.MessageProcessor

object FConsumer extends App {
  override def main(args: Array[String]): Unit = {
    val rabbitConsumer = new FQueueConsumer()
    rabbitConsumer.declareQueue()

    val messageProcessor = buildMessageProcessor()

    val onMessage = (message: String) => 
      Await.result(messageProcessor.processMessage(message), Duration.Inf)
    
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

  private def buildMessageProcessor(): MessageProcessor = {
    val rabbitFraudPublisher = new FMessagePublisher()
    rabbitFraudPublisher.declareQueue()

    val sqlRepository = new FModelsRepository()
    val eventParser = EventParser()
    val eventsRepository = new FEventsRepository()

    val eventProcessor = new EventProcessor(eventsRepository, sqlRepository, rabbitFraudPublisher)

    MessageProcessor(eventParser, eventProcessor)
  }

}
