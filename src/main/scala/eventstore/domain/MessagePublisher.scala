package eventstore.domain

import scala.concurrent.Future

trait MessagePublisher {
  def publish(message: String): Future[Unit]
}
