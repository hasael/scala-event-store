package eventstore.domain

import eventstore.readmodels.TransactionModel

import scala.util.Try

trait ModelsRepository {
  def upsertTransaction(transactionMode: TransactionModel): Try[Unit]
}
