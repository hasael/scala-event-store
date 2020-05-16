package eventstore.readmodels

case class TransactionModel(transactionId: String, amount: Double, currency: String,
                            paymentType: String, status: String, transactionTime: String)