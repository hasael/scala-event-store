package eventstore.domain

import eventstore.context.Types.LoggedFuture
import eventstore.readmodels.TransactionModel

trait ModelsRepository {
  def upsertTransaction(transactionMode: TransactionModel): LoggedFuture[Unit]
}
