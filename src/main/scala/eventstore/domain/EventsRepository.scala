package eventstore.domain

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}

import scala.util.Try

trait EventsRepository {
  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit]

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit]

  def insertPaymentPending(paymentPending: PaymentPending): Try[Unit]
}
