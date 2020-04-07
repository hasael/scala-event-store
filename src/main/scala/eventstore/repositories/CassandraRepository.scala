package eventstore.repositories

import java.util.UUID

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}
import io.getquill.{CassandraSyncContext, SnakeCase}

import scala.util.Try

case class Events(id: UUID, content: String, amount: BigDecimal, currency: String, eventType: String, paymentType: String,
                  transactionId: UUID, transactionTime: String)

class CassandraRepository {
  lazy val ctx = new CassandraSyncContext(SnakeCase, "ctx")

  import ctx._

  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), paymentAccepted.eventName, paymentAccepted.amount, paymentAccepted.currency, "PaymentAccepted", paymentAccepted.paymentData.paymentType
      , UUID.fromString(paymentAccepted.transactionId), paymentAccepted.transactionTime)))))

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), paymentDeclined.eventName, paymentDeclined.amount, paymentDeclined.currency, "PaymentDeclined", paymentDeclined.paymentData.paymentType,
      UUID.fromString(paymentDeclined.transactionId), paymentDeclined.transactionTime)))))

  def insertPaymentPending(paymentPending: PaymentPending): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), paymentPending.eventName, paymentPending.amount, paymentPending.currency, "PaymentPending", paymentPending.paymentType,
      UUID.fromString(paymentPending.transactionId), paymentPending.transactionTime)))))

}
