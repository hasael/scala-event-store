package eventstore.parsers

import eventstore.events.{PaymentAccepted, PaymentEvent}
import play.api.libs.json.{JsString, Json}

import scala.util.{Failure, Success, Try}

object EventParser {

  def parseEvent(message: String): Try[PaymentEvent] = {
    val jsonObject = Json.parse(message)
    val jsEventType = jsonObject \ "eventType"
    val eventType = jsEventType.toOption.flatMap(jsValue => jsValue match {
      case JsString(value) => Some(value)
      case _ => None
    })

    val event = eventType.flatMap(value => value match {
      case "PaymentAccepted" => Some(jsonObject.as[PaymentAccepted])
      case _ => None
    })

    event.map(ev => Success(ev))
      .getOrElse(Failure(new Exception("Could not find event type")))
  }
}
