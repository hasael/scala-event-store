package eventstore.events

trait PaymentEvent {
  def eventName: String
}
