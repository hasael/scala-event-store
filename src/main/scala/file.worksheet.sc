import scala.concurrent._
import cats.effect.ExitCase.Canceled
import cats.effect.ExitCode
import cats.effect.IOApp
import cats.effect.Resource
import scala.concurrent.ExecutionContext
import cats.effect.ContextShift
import cats.effect.IO
import java.util.concurrent.ScheduledExecutorService
import scala.concurrent.duration._

implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

def putStrlLn(value: String) = IO(println(value))
val readLn = IO(scala.io.StdIn.readLine)

val a = for {
  _ <- putStrlLn("What's your name?")
  n <- readLn
  _ <- putStrlLn(s"Hello, $n!")
} yield ()

//a.unsafeRunSync()

val b = IO.async[String] { cb =>
  {
    cb(Right("hello"))
  }
}

val c = IO.cancelable[String] { cb =>
  {
    IO(cb(Right("hello2")))
  }
}

val d = IO.never
b.unsafeRunSync()
b.start.flatMap(fiber => fiber.join)
IO.race(IO.never, b).unsafeRunSync()
//c.unsafeRunSync()
//b.unsafeRunCancelable(_ => ())

IO.pure(123).map(n => println(s"NOT RECOMMENDED! $n")).unsafeRunSync()

val launchMissiles: IO[Unit] = IO.raiseError(new Exception("boom!"))
val runToBunker = IO(println("To the bunker!!!"))

for {
  fiber <- launchMissiles.start
  _ <- runToBunker.handleErrorWith { error =>
    // Retreat failed, cancel launch (maybe we should
    // have retreated to our bunker before the launch?)
    fiber.cancel *> IO.raiseError(error)
  }
  aftermath <- fiber.join
} yield aftermath

def mkResource(s: String) = {
  val acquire = IO(println(s"Acquiring $s")) *> IO.pure(s)

  def release(s: String) = IO(println(s"Releasing $s"))

  Resource.make(acquire)(release)
}
val r = for {
  outer <- mkResource("outer")
  inner <- mkResource("inner")
} yield (outer, inner)

r.use { case (a, b) => IO(println(s"Using $a and $b")) }.unsafeRunSync

//Resource.make(s => IO(println("acquiring "+s)) *> IO.pure(s))(s => IO[Unit](println("releaed "+s)))

object Main extends IOApp {

  def loop(n: Int): IO[ExitCode] =
    IO.suspend {
      if (n < 10)
        IO.sleep(1.second) *> IO(println(s"Tick: $n")) *> loop(n + 1)
      else
        IO.pure(ExitCode.Success)
    }

  def run(args: List[String]): IO[ExitCode] =
    loop(0).guaranteeCase {
      case Canceled =>
        IO(println("Interrupted: releasing and exiting!"))
      case _ =>
        IO(println("Normal exit!"))
    }
}



object testingFuture {
  import scala.concurrent.ExecutionContext.Implicits.global
  def run() {

    Future { print("operation future2") }

    Thread.sleep(11000)

  }
}

//testingFuture.run()
