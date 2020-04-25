package eventstore.domain

import scala.util.Try

trait MessagePublisher {
  def publish(message: String): Try[Unit]
}
