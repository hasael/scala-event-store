package eventstore.rabbitmq

import com.rabbitmq.client.ConnectionFactory

class RabbitPublisher(hostName: String, user: String, pass: String, port: Int) {
  val factory = new ConnectionFactory()
  factory.setHost(hostName)
  factory.setPort(port)
  factory.setUsername(user)
  factory.setPassword(pass)

  val connection = factory.newConnection()
  val channel = connection.createChannel()

  def declareQueue(queueName: String) = channel.queueDeclare(queueName, false, false, false, null)

  def publish(exchange: String, queueName: String, message: String) = {
    channel.basicPublish(exchange, queueName, null, message.getBytes)
  }

}

object RabbitPublisher {
  def apply(hostName: String, user: String, pass: String, port: Int): RabbitPublisher = new RabbitPublisher(hostName, user, pass, port)
}
