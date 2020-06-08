package eventstore.dummy

import eventstore.domain.EventsRepository
import eventstore.events.PaymentAccepted
import scala.concurrent.Future
import eventstore.events.PaymentDeclined
import scala.concurrent.Future
import eventstore.events.PaymentPending
import scala.concurrent.Future
import eventstore.context.FutureContext._

class FEventsRepository extends EventsRepository {

  override def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Future[Unit] = Future(
      Thread.sleep(1000)
  )

  override def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Future[Unit] = Future(
      Thread.sleep(1000)
  )

  override def insertPaymentPending(paymentPending: PaymentPending): Future[Unit] = Future(
      Thread.sleep(1000)
  )
  
}
