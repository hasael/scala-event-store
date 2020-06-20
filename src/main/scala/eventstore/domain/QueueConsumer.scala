package eventstore.domain

trait QueueConsumer {
  
  def startConsumer[A](queueName: String, autoAck: Boolean, onMessage: (String => A), onCancel: String => Unit)
  
  def declareQueue()
}
