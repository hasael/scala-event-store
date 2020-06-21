package eventstore.domain

import cats.effect.Sync
import cats.implicits._
import eventstore.events._
import eventstore.readmodels.TransactionModel
import play.api.libs.json.Json

import scala.util.Try

class EventProcessor[F[_]: Sync](
    eventsRepository: EventsRepository[F],
    modelsRepository: ModelsRepository[F],
    fraudCheckPublisher: MessagePublisher[F]
) {

  def processEvent(paymentEvent: PaymentEvent): F[Unit] = {
    paymentEvent match {
      case paymentAccepted: PaymentAccepted => handlePaymentAccepted(paymentAccepted)
      case paymentDeclined: PaymentDeclined => handlePaymentDeclined(paymentDeclined)
      case paymentPending: PaymentPending   => handlePaymentPending(paymentPending)
      case _                                => Sync[F].delay(throw new Throwable("Payment event type not mapped"))
    }
  }

  private def createPaymentFailed(paymentDeclined: PaymentDeclined): Try[String] = {
    val paymentFailed = PaymentFailed(
      paymentDeclined.transactionId,
      paymentDeclined.amount,
      paymentDeclined.userId,
      paymentDeclined.currency,
      paymentDeclined.reason,
      paymentDeclined.paymentData.paymentType,
      paymentDeclined.paymentData.cardId,
      paymentDeclined.transactionTime
    )

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

  private def handlePaymentDeclined(paymentDeclined: PaymentDeclined): F[Unit] = {
    for{
      _ <- eventsRepository.insertPaymentDeclined(paymentDeclined)
      _ <- modelsRepository.upsertTransaction(eventToReadModel(paymentDeclined))
      _ <- publishMessage(paymentDeclined)
    } yield()
  }

  private def publishMessage(paymentDeclined: PaymentDeclined): F[Unit] = {    
    for {
      message <- Sync[F].fromTry[String](createPaymentFailed(paymentDeclined))
      result <- fraudCheckPublisher.publish(message)
    } yield result
  }

  private def handlePaymentAccepted(paymentAccepted: PaymentAccepted): F[Unit] = {
      for{
        _ <-  eventsRepository.insertPaymentAccepted(paymentAccepted)
        _ <- modelsRepository.upsertTransaction(eventToReadModel(paymentAccepted))
      } yield()
  }

  private def handlePaymentPending(paymentPending: PaymentPending): F[Unit] = {
    for{
      _ <- eventsRepository.insertPaymentPending(paymentPending)
      _ <- modelsRepository.upsertTransaction(eventToReadModel(paymentPending))
    } yield()
  }
}
