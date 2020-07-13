package eventstore.rabbitmq

import cats.effect.Sync
import eventstore.domain.MessagePublisher

class RabbitPublisher[F[_]: Sync](client: RabbitMqClient[F], exchange: String, routingKey: String) extends MessagePublisher[F] {

  def publish(message: String): F[Unit] = {
    client.publish(message, exchange, routingKey)
  }
}

object RabbitPublisher {
  def apply[F[_]: Sync](client: RabbitMqClient[F], exchange: String, routingKey: String): RabbitPublisher[F] =
    new RabbitPublisher[F](client, exchange, routingKey)
}
