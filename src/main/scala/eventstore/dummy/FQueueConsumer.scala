package eventstore.dummy

import eventstore.domain.QueueConsumer
import java.{util => ju}
import scala.util.Random
import java.time.Instant
import java.time.ZoneId

class FQueueConsumer extends QueueConsumer {

  override def startConsumer[A](queueName: String, autoAck: Boolean, onMessage: String => A, onCancel: String => Unit) = {
    while (true) {
      onMessage(RandomEventCreator().createRandomEvent())
      Thread.sleep(1000)
    }
  }

  override def declareQueue(): Unit = {}
}
