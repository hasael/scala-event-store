package eventstore.dummy

import cats.effect.Sync
import eventstore.domain.ModelsRepository
import eventstore.readmodels.TransactionModel
import io.chrisdavenport.log4cats.Logger
import cats.implicits._

class FModelsRepository[F[_] : Sync : Logger] extends ModelsRepository[F] {

  override def upsertTransaction(transactionMode: TransactionModel): F[Unit] =

    Logger[F].info("Calling upsert transaction") >>
      Sync[F].delay {
        Thread.sleep(2000)
      } >> Logger[F].info("Upserted transaction with id " + transactionMode.transactionId)

}
