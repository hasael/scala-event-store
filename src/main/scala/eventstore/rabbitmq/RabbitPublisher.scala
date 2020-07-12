package eventstore.rabbitmq

import com.rabbitmq.client.ConnectionFactory
import eventstore.domain.MessagePublisher
import cats.effect.Sync

class RabbitPublisher[F[_]: Sync](hostName: String, user: String, pass: String, vhost: String, port: Int, exchange: String, queueName: String)
    extends MessagePublisher[F] {
  val factory = new ConnectionFactory()
  factory.setHost(hostName)
  factory.setPort(port)
  factory.setUsername(user)
  factory.setPassword(pass)
  factory.setVirtualHost(vhost)

  val connection = factory.newConnection()
  val channel = connection.createChannel()

  def declareQueue() = channel.queueDeclare(queueName, false, false, false, null)

  def publish(message: String): F[Unit] = {
    Sync[F].delay(channel.basicPublish(exchange, queueName, null, message.getBytes))
  }

}

object RabbitPublisher {
  def apply[F[_]: Sync](
      hostName: String,
      user: String,
      pass: String,
      vhost: String,
      port: Int,
      exchange: String,
      queueName: String
  ): RabbitPublisher[F] =
    new RabbitPublisher[F](hostName, user, pass, vhost, port, exchange, queueName)
}
