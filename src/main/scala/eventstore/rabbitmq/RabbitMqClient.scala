package eventstore.rabbitmq

import java.util.concurrent.Executors

import cats.data.NonEmptyList
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Resource}
import cats.implicits._
import dev.profunktor.fs2rabbit.config.{Fs2RabbitConfig, Fs2RabbitNodeConfig}
import dev.profunktor.fs2rabbit.interpreter.RabbitClient
import dev.profunktor.fs2rabbit.model.{AmqpEnvelope, QueueName}
import eventstore.domain.{PaymentMessage, QueueClient}
import fs2.Pipe

class RabbitMqClient[F[_]: ConcurrentEffect: ContextShift](
    hostName: String,
    user: String,
    pass: String,
    vhost: String,
    port: Int
) extends QueueClient[F] {

  val config = Fs2RabbitConfig(
    virtualHost = vhost,
    nodes = NonEmptyList.one(
      Fs2RabbitNodeConfig(
        host = hostName,
        port = port
      )
    ),
    username = Some(user),
    password = Some(pass),
    ssl = false,
    connectionTimeout = 300,
    requeueOnNack = false,
    internalQueueSize = Some(500),
    automaticRecovery = true
  )

  val blockerResource =
    Resource
      .make(ConcurrentEffect[F].delay(Executors.newCachedThreadPool()))(es => ConcurrentEffect[F].delay(es.shutdown()))
      .map(Blocker.liftExecutorService)

  def autoAckConsumer[A](
      queueName: String,
      onMessage: PaymentMessage => F[Unit]
  ): F[Unit] = {

    blockerResource.use { blocker =>
      ConcurrentEffect[F].flatMap(RabbitClient[F](config, blocker))(client =>
        client.createConnectionChannel.use { implicit channel =>
          client
            .createAutoAckConsumer[String](QueueName(queueName))
            .flatMap(stream => stream.through(toPaymentMessage).evalMap(onMessage).compile.drain)
        }
      )
    }
  }

  private def toPaymentMessage[F[_]]: Pipe[F, AmqpEnvelope[String], PaymentMessage] = in => {
    in.map(ack => PaymentMessage(ack.payload))
  }
}

object RabbitMqClient {
  def apply[F[_]: ConcurrentEffect: ContextShift](
      hostName: String,
      user: String,
      pass: String,
      vhost: String,
      port: Int
  ): RabbitMqClient[F] =
    new RabbitMqClient(hostName, user, pass, vhost, port)
}
