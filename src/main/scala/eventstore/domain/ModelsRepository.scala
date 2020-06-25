package eventstore.domain

import eventstore.readmodels.TransactionModel

trait ModelsRepository[F[_]] {
  def upsertTransaction(transactionMode: TransactionModel): F[Unit]
}
