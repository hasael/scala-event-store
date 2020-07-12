package eventstore.domain

trait MessagePublisher[F[_]] {
  def publish(message: String): F[Unit]
}
