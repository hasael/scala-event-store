package eventstore.dummy

import eventstore.domain.QueueConsumer

class FQueueConsumer extends QueueConsumer {

  override def startConsumer[A](queueName: String, autoAck: Boolean, onMessage: String => A, onCancel: String => Unit) = {
    while (true) {
      onMessage(RandomEventCreator().createRandomEvent())
      Thread.sleep(1000)
    }
  }

  override def declareQueue(): Unit = {}
}
