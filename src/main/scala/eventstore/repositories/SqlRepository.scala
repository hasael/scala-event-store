package eventstore.repositories

import eventstore.domain.ModelsRepository
import eventstore.readmodels.TransactionModel

import scala.util.Try

class SqlRepository extends ModelsRepository {
  override def upsertTransaction(transactionMode: TransactionModel): Try[Unit] = Try()
}
