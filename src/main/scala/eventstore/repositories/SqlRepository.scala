package eventstore.repositories

import java.sql.DriverManager

import eventstore.domain.ModelsRepository
import eventstore.readmodels.TransactionModel

import scala.concurrent.Future
import eventstore.context.FutureContext._

class SqlRepository(host: String, port: Int, schema: String, username: String, password: String) extends ModelsRepository {
  override def upsertTransaction(transactionMode: TransactionModel): Future[Unit] = Future {

    val url = "jdbc:mysql://" + host + ":" + port + "/" + schema
    val connection = DriverManager.getConnection(url, username, password)
    val statement = connection.prepareStatement("REPLACE INTO TRANSACTIONS VALUES( ?, ?, ?, ?, ?, ?)")
    statement.setString(1, transactionMode.transactionId)
    statement.setDouble(2, transactionMode.amount)
    statement.setString(3, transactionMode.currency)
    statement.setString(4, transactionMode.paymentType)
    statement.setString(5, transactionMode.status)
    statement.setString(6, transactionMode.transactionTime)
    statement.execute()
  }
}