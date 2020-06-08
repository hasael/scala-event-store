package eventstore.context

import cats.data.WriterT
import cats.instances.all._
import cats.{Applicative, Functor}
import eventstore.context.FutureContext._
import eventstore.context.Types.LoggedFuture

import scala.concurrent.Future

object Types {
  type LoggedFuture[A] = WriterT[Future, List[String], A]
}

object LoggedFuture {
  def apply[A](a: A): LoggedFuture[A] = {
    new WriterT[Future, List[String], A](Future(a).map(v => (List.empty[String], v)))
  }

  def sequenceF[A](l: List[LoggedFuture[A]]): WriterT[Future, List[String], List[A]] = {
    new WriterT[Future, List[String], List[A]](
      Future.sequence(l.map(_.run)).map(value => {
        val logs = value.foldLeft(List.empty[String])((acc, v) => acc ++ v._1)
        val results = value.map(v => v._2)
        (logs, results)
      }))
  }

  implicit class LoggedFutureSyntax[A](l: LoggedFuture[A]) {
    def tellLine(s: String): LoggedFuture[A] =
      l.tell(List(s))

    def tellLine(func: A => String): LoggedFuture[A] =
      l.mapBoth((list, result) => (list ++ List(func(result)), result))
  }

}

object Syntax {

  implicit class LoggedSyntax[F[_], A](f: F[A]) {
    def asLogged()(implicit app: Applicative[F]): WriterT[F, List[String], A] = {
      WriterT.liftF[F, List[String], A](f)
    }

    def asLogged(s: String)(implicit fu: Functor[F]): WriterT[F, List[String], A] = {
      new WriterT[F, List[String], A](fu.map(f)(a => (List(s), a)))
    }

    def asLogged(func: A => String)(implicit fu: Functor[F]): WriterT[F, List[String], A] = {
      new WriterT[F, List[String], A](fu.map(f)(a => (List(func(a)), a)))
    }
  }

}