package eventstore.repositories

import java.util.UUID

import cats.effect.Sync
import cats.implicits._
import com.datastax.oss.driver.api.core.CqlSession
import eventstore.domain.EventsRepository
import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}
import io.chrisdavenport.log4cats.Logger
import play.api.libs.json.Json

case class Events(
    id: UUID,
    content: String,
    amount: Double,
    currency: String,
    eventType: String,
    paymentType: String,
    transactionId: UUID,
    transactionTime: String
)

class CassandraRepository[F[_]: Sync: Logger](session: CqlSession) extends EventsRepository[F] {

  lazy val schemaInsertStatement =
    """
      |INSERT INTO events(id, event_amount, content, currency, event_type, payment_type, transaction_id, transaction_time)
      |VALUES(?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin
  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): F[Unit] =
    for {
      result <- Sync[F].delay {
        val event = Events(
          UUID.randomUUID(),
          Json.stringify(Json.toJson(paymentAccepted)),
          paymentAccepted.amount,
          paymentAccepted.currency,
          "PaymentAccepted",
          paymentAccepted.paymentData.paymentType,
          UUID.fromString(paymentAccepted.transactionId),
          paymentAccepted.transactionTime
        )
        lazy val insertStatement = session.prepare(schemaInsertStatement)
        val statement = insertStatement
          .bind()
          .setUuid("id", event.id)
          .setString("content", event.content)
          .setDouble("event_amount", event.amount)
          .setString("currency", event.currency)
          .setString("event_type", event.eventType)
          .setString("payment_type", event.paymentType)
          .setUuid("transaction_id", event.transactionId)
          .setString("transaction_time", event.transactionTime)
        session.execute(statement)
      }
      _ <- Logger[F].info(s"Inserted paymentAccepted to Cassandra repository. Result ${result.wasApplied()}")
    } yield ()

  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): F[Unit] =
    for {
      result <- Sync[F].delay {
        val event = Events(
          UUID.randomUUID(),
          Json.stringify(Json.toJson(paymentDeclined)),
          paymentDeclined.amount,
          paymentDeclined.currency,
          "PaymentDeclined",
          paymentDeclined.paymentData.paymentType,
          UUID.fromString(paymentDeclined.transactionId),
          paymentDeclined.transactionTime
        )
        lazy val insertStatement = session.prepare(schemaInsertStatement)
        val statement = insertStatement
          .bind()
          .setUuid("id", event.id)
          .setString("content", event.content)
          .setDouble("event_amount", event.amount)
          .setString("currency", event.currency)
          .setString("event_type", event.eventType)
          .setString("payment_type", event.paymentType)
          .setUuid("transaction_id", event.transactionId)
          .setString("transaction_time", event.transactionTime)
        session.execute(statement)
      }
      _ <- Logger[F].info(s"Inserted paymentDeclined to Cassandra repository. Result ${result.wasApplied()}")
    } yield ()

  def insertPaymentPending(paymentPending: PaymentPending): F[Unit] =
    for {
      result <- Sync[F].delay {
        val event = Events(
          UUID.randomUUID(),
          Json.stringify(Json.toJson(paymentPending)),
          paymentPending.amount,
          paymentPending.currency,
          "PaymentPending",
          paymentPending.paymentType,
          UUID.fromString(paymentPending.transactionId),
          paymentPending.transactionTime
        )
        lazy val insertStatement = session.prepare(schemaInsertStatement)
        val statement = insertStatement
          .bind()
          .setUuid("id", event.id)
          .setString("content", event.content)
          .setDouble("event_amount", event.amount)
          .setString("currency", event.currency)
          .setString("event_type", event.eventType)
          .setString("payment_type", event.paymentType)
          .setUuid("transaction_id", event.transactionId)
          .setString("transaction_time", event.transactionTime)
        session.execute(statement)
      }
      _ <- Logger[F].info(s"Inserted paymentPending to Cassandra repository. Result ${result.wasApplied()}")
    } yield ()
}
