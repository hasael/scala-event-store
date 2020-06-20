package eventstore.domain

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}

import cats.effect.Sync

trait EventsRepository[F[_]] {
  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): F[Unit]

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): F[Unit]

  def insertPaymentPending(paymentPending: PaymentPending): F[Unit]
}
