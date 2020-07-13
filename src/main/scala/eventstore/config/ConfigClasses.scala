package eventstore.config

case class RabbitConfig(queue: String, host: String, username: String, password: String, port: Int, vhost: String)

case class CassandraConfig(keyspace: String, username: String, password: String, secureConnectionPath: String)

case class MySqlConfig(host: String, username: String, password: String, port: Int, schema: String)

case class Config(rabbitPaymentConfig: RabbitConfig, rabbitFraundConfig: RabbitConfig, mySqlConfig: MySqlConfig, cassandraConfig: CassandraConfig)
