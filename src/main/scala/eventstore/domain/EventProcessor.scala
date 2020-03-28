package eventstore.domain

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentEvent, PaymentPending}
import eventstore.repositories.CassandraRepository

import scala.util.{Failure, Try}

class EventProcessor(cassandraRepository: CassandraRepository) {

  def processEvent(paymentEvent: PaymentEvent): Try[Unit] = {
    paymentEvent match {
      case paymentAccepted: PaymentAccepted => cassandraRepository.insertPaymentAccepted(paymentAccepted)
      case paymentDeclined: PaymentDeclined => cassandraRepository.insertPaymentDeclined(paymentDeclined)
      case paymentPending: PaymentPending => cassandraRepository.insertPaymentPending(paymentPending)
      case _ => Failure(new Throwable("Payment event type not mapped"))
    }
  }
}
