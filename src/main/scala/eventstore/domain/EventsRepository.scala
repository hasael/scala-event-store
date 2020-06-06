package eventstore.domain

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}

import scala.concurrent.Future

trait EventsRepository {
  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Future[Unit]

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Future[Unit]

  def insertPaymentPending(paymentPending: PaymentPending): Future[Unit]
}
