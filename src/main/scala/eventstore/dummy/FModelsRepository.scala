package eventstore.dummy

import eventstore.domain.ModelsRepository
import eventstore.context.Types
import eventstore.readmodels.TransactionModel
import eventstore.context.LoggedFuture

class FModelsRepository extends ModelsRepository {

  override def upsertTransaction(transactionMode: TransactionModel): Types.LoggedFuture[Unit] = LoggedFuture{
      Thread.sleep(1000)
  }

  
}
