package eventstore.dummy

import eventstore.domain.MessagePublisher
import cats.Id
import cats.effect.Sync

class FMessagePublisher[F[_]:Sync] extends MessagePublisher[F] {

  override def publish(message: String): F[Unit] = Sync[F].delay(Thread.sleep(1000))
  override def declareQueue() = {}
}
