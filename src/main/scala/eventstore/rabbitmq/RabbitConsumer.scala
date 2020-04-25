package eventstore.rabbitmq

import com.rabbitmq.client.{CancelCallback, ConnectionFactory, DeliverCallback}

class RabbitConsumer(hostName: String, user: String, pass: String, port: Int, exchange: String, queueName: String) {
  val factory = new ConnectionFactory()
  factory.setHost(hostName)
  factory.setPort(port)
  factory.setUsername(user)
  factory.setPassword(pass)

  val connection = factory.newConnection()
  val channel = connection.createChannel()

  def declareQueue() = channel.queueDeclare(queueName, false, false, false, null)

  def startConsumer(queueName: String, autoAck: Boolean, callback: DeliverCallback, cancel: CancelCallback) =
    channel.basicConsume(queueName, autoAck, callback, cancel)

  def startConsumer(queueName: String, autoAck: Boolean, onMessage: (String => Unit), onCancel: String => Unit) = {
    val callback: DeliverCallback = (consumerTag, delivery) => {
      val message = new String(delivery.getBody, "UTF-8")
      println(s"Received $message with tag $consumerTag")
      onMessage(message)
    }

    val cancel: CancelCallback = consumerTag => onCancel(consumerTag)

    channel.basicConsume(queueName, autoAck, callback, cancel)
  }

  def onClose() = {
    channel.close()
    connection.close()
  }
}

object RabbitConsumer {
  def apply(hostName: String, user: String, pass: String, port: Int, exchange: String, queueName: String): RabbitConsumer = new RabbitConsumer(hostName, user, pass, port, exchange, queueName)
}