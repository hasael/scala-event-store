package eventstore.context

import cats.Functor
import cats.data.WriterT

import scala.concurrent.Future

object Types {
  type LoggedFuture[A] = WriterT[Future, List[String], A]
}

object Syntax {

  implicit class LoggedSyntax[F[_], A](f: F[A]) {
    def asLogged()(implicit fu: Functor[F]): WriterT[F, List[String], A] = {
      new WriterT[F, List[String], A](fu.map(f)(a => (List.empty[String], a)))
    }

    def asLogged(s: String)(implicit fu: Functor[F]): WriterT[F, List[String], A] = {
      new WriterT[F, List[String], A](fu.map(f)(a => (List(s), a)))
    }
  }

}