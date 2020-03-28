package eventstore.repositories

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentEvent, PaymentPending}

import scala.util.{Success, Try}

class CassandraRepository {

  def insertPaymentAccepted(paymentAccepted: PaymentAccepted): Try[Unit] = Success()
  def insertPaymentDeclined(paymentDeclined: PaymentDeclined): Try[Unit] = Success()
  def insertPaymentPending(paymentPending: PaymentPending): Try[Unit] = Success()

}
