package eventstore.dummy

import eventstore.domain.EventsRepository
import eventstore.events.PaymentAccepted
import eventstore.events.PaymentDeclined
import eventstore.events.PaymentPending
import cats.Id
import cats.effect.Sync

class FEventsRepository[F[_]: Sync] extends EventsRepository[F] {

  override def insertPaymentAccepted(paymentAccepted: PaymentAccepted): F[Unit] = (
    Sync[F].delay(Thread.sleep(1000))
  )

  override def insertPaymentDeclined(paymentDeclined: PaymentDeclined): F[Unit] = (
    Sync[F].delay(Thread.sleep(1000))
  )

  override def insertPaymentPending(paymentPending: PaymentPending): F[Unit] = (
    Sync[F].delay(Thread.sleep(1000))
  )

}
