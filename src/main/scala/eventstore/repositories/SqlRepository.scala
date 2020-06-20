package eventstore.repositories

import java.sql.DriverManager

import cats.instances.list._
import eventstore.domain.ModelsRepository
import eventstore.readmodels.TransactionModel
import cats.effect.Sync

class SqlRepository[F[_]:Sync](host: String, port: Int, schema: String, username: String, password: String) extends ModelsRepository[F] {
  override def upsertTransaction(transactionMode: TransactionModel): F[Unit] = {
    Sync[F].delay {
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
    //.tellLine(result => "Upsert transaction data to mysql " + schema + " schema. Result " + result)
     // .map(_ => Unit)

  }
}