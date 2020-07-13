package eventstore.domain

import fs2.Stream

trait QueueClient[F[_]] {
  def autoAckConsumer[A](queueName: String, onMessage: PaymentMessage => F[Unit]): F[Unit]
}
