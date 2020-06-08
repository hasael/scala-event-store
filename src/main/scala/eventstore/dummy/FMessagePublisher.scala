package eventstore.dummy

import eventstore.domain.MessagePublisher
import scala.concurrent.Future
import eventstore.context.FutureContext._

class FMessagePublisher extends MessagePublisher {

  override def publish(message: String): Future[Unit] = Future(Thread.sleep(1000))
  override def declareQueue() = {}
}
