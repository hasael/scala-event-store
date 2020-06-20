package eventstore.dummy

import eventstore.domain.ModelsRepository
import eventstore.context.Types
import eventstore.readmodels.TransactionModel
import cats.Id
import cats.effect.Sync

class FModelsRepository[F[_]:Sync] extends ModelsRepository[F] {

  override def upsertTransaction(transactionMode: TransactionModel): F[Unit] = Sync[F].delay{
      Thread.sleep(1000)
  }
  //.tellLine("Upserted transaction with id " + transactionMode.transactionId)
  
}
