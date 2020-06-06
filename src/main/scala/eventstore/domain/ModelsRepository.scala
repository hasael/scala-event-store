package eventstore.domain

import eventstore.readmodels.TransactionModel

import scala.concurrent.Future

trait ModelsRepository {
  def upsertTransaction(transactionMode: TransactionModel): Future[Unit]
}
