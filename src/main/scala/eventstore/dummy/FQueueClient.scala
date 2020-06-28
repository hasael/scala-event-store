package eventstore.dummy

import cats.Id
import eventstore.domain.{PaymentMessage, QueueClient}
import fs2.Stream
class FQueueClient extends QueueClient[Id] {

  override def autoAckConsumer[A](
      queueName: String,
      onMessage: PaymentMessage => Unit
  ): Unit = {
    while (true) {
      RandomEventCreator().createRandomEvent()
      Thread.sleep(1000)
    }
  }

}
