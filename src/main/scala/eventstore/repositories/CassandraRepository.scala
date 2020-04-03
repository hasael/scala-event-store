package eventstore.repositories

import java.util.UUID

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}
import io.getquill.{CassandraSyncContext, SnakeCase}
import io.getquill._

import scala.util.{Success, Try}

case class Events(id: UUID, content: String)

class CassandraRepository {
  lazy val ctx = new CassandraSyncContext(SnakeCase, "ctx")

  import ctx._

  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), paymentAccepted.eventName)))))

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), paymentDeclined.eventName)))))

  def insertPaymentPending(paymentPending: PaymentPending): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), paymentPending.eventName)))))

}
