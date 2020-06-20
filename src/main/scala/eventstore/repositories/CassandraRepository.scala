package eventstore.repositories

import java.util.UUID

import eventstore.domain.EventsRepository
import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}
import io.getquill.{CassandraSyncContext, SnakeCase}
import play.api.libs.json.Json

import cats.effect.Sync

case class Events(id: UUID, content: String, amount: Double, currency: String, eventType: String, paymentType: String,
                  transactionId: UUID, transactionTime: String)

class CassandraRepository[F[_]:Sync] extends EventsRepository[F] {
  lazy val ctx = new CassandraSyncContext(SnakeCase, "ctx")

  import ctx._

  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): F[Unit] =
    Sync[F].delay(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentAccepted)), paymentAccepted.amount, paymentAccepted.currency, "PaymentAccepted", paymentAccepted.paymentData.paymentType
      , UUID.fromString(paymentAccepted.transactionId), paymentAccepted.transactionTime)))))

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): F[Unit] =
    Sync[F].delay(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentDeclined)), paymentDeclined.amount, paymentDeclined.currency, "PaymentDeclined", paymentDeclined.paymentData.paymentType,
      UUID.fromString(paymentDeclined.transactionId), paymentDeclined.transactionTime)))))

  def insertPaymentPending(paymentPending: PaymentPending): F[Unit] =
    Sync[F].delay(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentPending)), paymentPending.amount, paymentPending.currency, "PaymentPending", paymentPending.paymentType,
      UUID.fromString(paymentPending.transactionId), paymentPending.transactionTime)))))

}
