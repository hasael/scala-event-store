package eventstore.repositories

import java.util.UUID

import eventstore.context.FutureContext._
import eventstore.domain.EventsRepository
import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}
import io.getquill.{CassandraSyncContext, SnakeCase}
import play.api.libs.json.Json

import scala.concurrent.Future

case class Events(id: UUID, content: String, amount: Double, currency: String, eventType: String, paymentType: String,
                  transactionId: UUID, transactionTime: String)

class CassandraRepository extends EventsRepository {
  lazy val ctx = new CassandraSyncContext(SnakeCase, "ctx")

  import ctx._

  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Future[Unit] =
    Future(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentAccepted)), paymentAccepted.amount, paymentAccepted.currency, "PaymentAccepted", paymentAccepted.paymentData.paymentType
      , UUID.fromString(paymentAccepted.transactionId), paymentAccepted.transactionTime)))))

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Future[Unit] =
    Future(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentDeclined)), paymentDeclined.amount, paymentDeclined.currency, "PaymentDeclined", paymentDeclined.paymentData.paymentType,
      UUID.fromString(paymentDeclined.transactionId), paymentDeclined.transactionTime)))))

  def insertPaymentPending(paymentPending: PaymentPending): Future[Unit] =
    Future(ctx.run(query[Events].insert(lift(Events(UUID.randomUUID(), Json.stringify(Json.toJson(paymentPending)), paymentPending.amount, paymentPending.currency, "PaymentPending", paymentPending.paymentType,
      UUID.fromString(paymentPending.transactionId), paymentPending.transactionTime)))))

}
