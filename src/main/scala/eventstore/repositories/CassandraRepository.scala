package eventstore.repositories

import java.util.UUID

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentEvent, PaymentPending}
import io.getquill.{CassandraSyncContext, SnakeCase}
import play.api.libs.json.Json

import scala.util.Try

case class Events(id: UUID, content: String, amount: BigDecimal, currency: String, eventType: String, paymentType: String,
                  transactionId: UUID, transactionTime: String)

class CassandraRepository {
  lazy val ctx = new CassandraSyncContext(SnakeCase, "ctx")

  import ctx._

  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentAccepted)), paymentAccepted.amount, paymentAccepted.currency, "PaymentAccepted", paymentAccepted.paymentData.paymentType
      , UUID.fromString(paymentAccepted.transactionId), paymentAccepted.transactionTime)))))

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentDeclined)), paymentDeclined.amount, paymentDeclined.currency, "PaymentDeclined", paymentDeclined.paymentData.paymentType,
      UUID.fromString(paymentDeclined.transactionId), paymentDeclined.transactionTime)))))

  def insertPaymentPending(paymentPending: PaymentPending): Try[Unit] =
    Try(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentPending)), paymentPending.amount, paymentPending.currency, "PaymentPending", paymentPending.paymentType,
      UUID.fromString(paymentPending.transactionId), paymentPending.transactionTime)))))

}
