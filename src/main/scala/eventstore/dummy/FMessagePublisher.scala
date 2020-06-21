package eventstore.dummy

import cats.effect.Sync
import cats.implicits._
import eventstore.domain.MessagePublisher
import io.chrisdavenport.log4cats.Logger

class FMessagePublisher[F[_] : Sync : Logger] extends MessagePublisher[F] {

  override def publish(message: String): F[Unit] = Sync[F].delay(Thread.sleep(1000)) >> Logger[F].info(s"Published message $message")

  override def declareQueue() = {}
}
