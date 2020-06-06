package eventstore.rabbitmq

import com.rabbitmq.client.ConnectionFactory
import eventstore.domain.MessagePublisher
import eventstore.context.FutureContext._
import scala.concurrent.Future

class RabbitPublisher(hostName: String, user: String, pass: String, port: Int, exchange: String, queueName: String) extends MessagePublisher {
  val factory = new ConnectionFactory()
  factory.setHost(hostName)
  factory.setPort(port)
  factory.setUsername(user)
  factory.setPassword(pass)

  val connection = factory.newConnection()
  val channel = connection.createChannel()

  def declareQueue() = channel.queueDeclare(queueName, false, false, false, null)

  def publish(message: String): Future[Unit] = {
    Future(channel.basicPublish(exchange, queueName, null, message.getBytes))
  }

}

object RabbitPublisher {
  def apply(hostName: String, user: String, pass: String, port: Int, exchange: String, queueName: String): RabbitPublisher = new RabbitPublisher(hostName, user, pass, port, exchange, queueName)
}
