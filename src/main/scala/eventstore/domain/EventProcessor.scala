package eventstore.domain

import eventstore.events._
import eventstore.readmodels.TransactionModel
import play.api.libs.json.Json

import scala.util.{Failure, Try}

class EventProcessor(eventsRepository: EventsRepository, modelsRepository: ModelsRepository, fraudCheckPublisher: MessagePublisher) {

  def processEvent(paymentEvent: PaymentEvent): Try[Unit] = {
    paymentEvent match {
      case paymentAccepted: PaymentAccepted => handlePaymentAccepted(paymentAccepted)
      case paymentDeclined: PaymentDeclined => handlePaymentDeclined(paymentDeclined)
      case paymentPending: PaymentPending => handlePaymentPending(paymentPending)
      case _ => Failure(new Throwable("Payment event type not mapped"))
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

  private def handlePaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit] = {
    for {
      _ <- eventsRepository.insertPaymentDeclined(paymentDeclined)
      message <- createPaymentFailed(paymentDeclined)
      _ <- modelsRepository.upsertTransaction(eventToReadModel(paymentDeclined))
      result <- fraudCheckPublisher.publish(message)
    } yield result
  }

  private def handlePaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit] = {
    for {
      _ <- eventsRepository.insertPaymentAccepted(paymentAccepted)
      result <- modelsRepository.upsertTransaction(eventToReadModel(paymentAccepted))
    } yield result
  }

  private def handlePaymentPending(paymentPending: PaymentPending): Try[Unit] = {
    for {
      _ <- eventsRepository.insertPaymentPending(paymentPending)
      result <- modelsRepository.upsertTransaction(eventToReadModel(paymentPending))
    } yield result
  }
}
