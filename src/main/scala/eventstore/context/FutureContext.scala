package eventstore.context

import scala.concurrent.ExecutionContext

object FutureContext {
  implicit val ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}
