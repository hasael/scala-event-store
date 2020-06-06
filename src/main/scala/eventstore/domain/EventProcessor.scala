package eventstore.domain

import cats.instances.future._
import eventstore.context.FutureContext._
import eventstore.context.Syntax._
import eventstore.context.Types.LoggedFuture
import eventstore.events._
import eventstore.readmodels.TransactionModel
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.Try

class EventProcessor(eventsRepository: EventsRepository, modelsRepository: ModelsRepository, fraudCheckPublisher: MessagePublisher) {

  def processEvent(paymentEvent: PaymentEvent): LoggedFuture[Unit] = {
    paymentEvent match {
      case paymentAccepted: PaymentAccepted => handlePaymentAccepted(paymentAccepted).asLogged("Processed paymentAccepted!")
      case paymentDeclined: PaymentDeclined => handlePaymentDeclined(paymentDeclined).asLogged
      case paymentPending: PaymentPending => handlePaymentPending(paymentPending).asLogged("Processed Payment Pending!")
      case _ => Future.failed[Unit](new Throwable("Payment event type not mapped")).asLogged
    }
  }

  private def createPaymentFailed(paymentDeclined: PaymentDeclined): Try[String] = {
    val paymentFailed = PaymentFailed(paymentDeclined.transactionId, paymentDeclined.amount, paymentDeclined.userId, paymentDeclined.currency,
      paymentDeclined.reason, paymentDeclined.paymentData.paymentType, paymentDeclined.paymentData.cardId, paymentDeclined.transactionTime)

    Try(Json.stringify(Json.toJson(paymentFailed)))
  }

  private def eventToReadModel(event: PaymentAccepted): TransactionModel = {
    TransactionModel(event.transactionId, event.amount, event.currency, event.paymentData.paymentType, "ACCEPTED", event.transactionTime)
  }

  private def eventToReadModel(event: PaymentDeclined): TransactionModel = {
    TransactionModel(event.transactionId, event.amount, event.currency, event.paymentData.paymentType, "DECLINED", event.transactionTime)
  }

  private def eventToReadModel(event: PaymentPending): TransactionModel = {
    TransactionModel(event.transactionId, event.amount, event.currency, event.paymentType, "PENDING", event.transactionTime)
  }

  private def handlePaymentDeclined(paymentDeclined: PaymentDeclined): Future[Unit] = {
    Future.sequence(Vector(
      eventsRepository.insertPaymentDeclined(paymentDeclined),
      modelsRepository.upsertTransaction(eventToReadModel(paymentDeclined)),
      publishMessage(paymentDeclined)
    )).map(_ => Unit)
  }

  private def publishMessage(paymentDeclined: PaymentDeclined): Future[Unit] = {
    for {
      message <- Future.fromTry(createPaymentFailed(paymentDeclined))
      result <- fraudCheckPublisher.publish(message)
    } yield result
  }

  private def handlePaymentAccepted(paymentAccepted: PaymentAccepted): Future[Unit] = {
    Future.sequence(Vector(
      eventsRepository.insertPaymentAccepted(paymentAccepted),
      modelsRepository.upsertTransaction(eventToReadModel(paymentAccepted))
    )).map(_ => Unit)
  }

  private def handlePaymentPending(paymentPending: PaymentPending): Future[Unit] = {
    Future.sequence(Vector(
      eventsRepository.insertPaymentPending(paymentPending),
      modelsRepository.upsertTransaction(eventToReadModel(paymentPending))
    )).map(_ => Unit)
  }
}
