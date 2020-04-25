package eventstore.domain

import eventstore.events._
import eventstore.repositories.CassandraRepository
import play.api.libs.json.Json

import scala.util.{Failure, Try}

class EventProcessor(cassandraRepository: CassandraRepository, fraudCheckPublisher: MessagePublisher) {

  def processEvent(paymentEvent: PaymentEvent): Try[Unit] = {
    paymentEvent match {
      case paymentAccepted: PaymentAccepted => cassandraRepository.insertPaymentAccepted(paymentAccepted)
      case paymentDeclined: PaymentDeclined => handlePaymentDeclined(paymentDeclined)
      case paymentPending: PaymentPending => cassandraRepository.insertPaymentPending(paymentPending)
      case _ => Failure(new Throwable("Payment event type not mapped"))
    }
  }

  private def createPaymentFailed(paymentDeclined: PaymentDeclined): Try[String] = {
    val paymentFailed = PaymentFailed(paymentDeclined.transactionId, paymentDeclined.amount, paymentDeclined.userId, paymentDeclined.currency,
      paymentDeclined.reason, paymentDeclined.paymentData.paymentType, paymentDeclined.paymentData.cardId, paymentDeclined.transactionTime)

    Try(Json.stringify(Json.toJson(paymentFailed)))
  }

  private def handlePaymentDeclined(paymentDeclined: PaymentDeclined) = {
    for {
      _ <- cassandraRepository.insertPaymentDeclined(paymentDeclined)
      message <- createPaymentFailed(paymentDeclined)
      result <- fraudCheckPublisher.publish(message)
    } yield result
  }
}
