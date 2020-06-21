package eventstore.dummy

import eventstore.domain.EventsRepository
import eventstore.events.PaymentAccepted
import eventstore.events.PaymentDeclined
import eventstore.events.PaymentPending
import cats.implicits._
import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger

class FEventsRepository[F[_] : Sync : Logger] extends EventsRepository[F] {

  override def insertPaymentAccepted(paymentAccepted: PaymentAccepted): F[Unit] = (
    Logger[F].info("Calling insertPaymentAccepted") >> Sync[F].delay(Thread.sleep(2000))
    ) >> Logger[F].info("Inserted paymentAccepted to events repository")

  override def insertPaymentDeclined(paymentDeclined: PaymentDeclined): F[Unit] = (
    Logger[F].info("Calling insertPaymentDeclined") >> Sync[F].delay(Thread.sleep(2000))
    ) >> Logger[F].info("Inserted paymentDeclined to events repository")

  override def insertPaymentPending(paymentPending: PaymentPending): F[Unit] = (
    Logger[F].info("Calling insertPaymentPending") >> Sync[F].delay(Thread.sleep(2000))
    ) >> Logger[F].info("Inserted paymentPending to events repository")

}
