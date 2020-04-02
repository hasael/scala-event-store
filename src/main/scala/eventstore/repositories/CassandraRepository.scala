package eventstore.repositories

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentPending}
import io.getquill.{CassandraSyncContext, SnakeCase}

import scala.util.{Success, Try}

class CassandraRepository {
  lazy val ctx = new CassandraSyncContext(SnakeCase, "ctx")
  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit] = Success()
  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit] = Success()
  def insertPaymentPending(paymentPending: PaymentPending): Try[Unit] = Success()

}
