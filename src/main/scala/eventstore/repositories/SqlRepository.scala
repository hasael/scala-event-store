package eventstore.repositories

import java.sql.DriverManager

import eventstore.domain.ModelsRepository
import eventstore.readmodels.TransactionModel

import scala.util.Try

class SqlRepository(host: String, port: Int, schema: String, username: String, password: String) extends ModelsRepository {
  override def upsertTransaction(transactionMode: TransactionModel): Try[Unit] = Try {

    val url = "jdbc:mysql://" + host + ":" + port + "/" + schema
    val connection = DriverManager.getConnection(url, username, password)

    val statement = connection.prepareStatement("INSERT INTO TRANSACTIONS VALUES(NULL, ?, ?, ?, ?, ?, ?)")
    statement.setString(1, transactionMode.transactionId)
    statement.setDouble(2, transactionMode.amount)
    statement.setString(3, transactionMode.currency)
    statement.setString(4, transactionMode.paymentType)
    statement.setString(5, transactionMode.status)
    statement.setString(6, transactionMode.transactionTime)
    statement.execute()

  }
}
