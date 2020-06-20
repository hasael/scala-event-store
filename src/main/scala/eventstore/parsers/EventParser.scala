package eventstore.parsers

import eventstore.events.{PaymentAccepted, PaymentDeclined, PaymentEvent, PaymentPending}
import play.api.libs.json.{JsString, JsValue, Json}

import scala.util.{Failure, Success, Try}

class EventParser {

  def parseEvent(message: String): Try[PaymentEvent] = {
    for {
      jsonObject <- Try(Json.parse(message))
      jsEventType <- getField(jsonObject, "eventType")
      event <- parseFromEventType(jsEventType, jsonObject)
    } yield event
  }

  private def getField(jsonObject: JsValue, fieldName: String): Try[JsValue] = {
    (jsonObject \ fieldName).toOption
      .map(ev => Success(ev))
      .getOrElse(Failure(new Exception(s"Could not find field $fieldName")))
  }

  private def parseFromEventType(jsEventType: JsValue, jsonObject: JsValue): Try[PaymentEvent] =
    jsEventType match {
      case JsString("PaymentAccepted") => Try(jsonObject.as[PaymentAccepted])
      case JsString("PaymentDeclined") => Try(jsonObject.as[PaymentDeclined])
      case JsString("PaymentPending")  => Try(jsonObject.as[PaymentPending])
      case _                           => Failure(new Exception("Event type field value was incorrect"))
    }
}
object EventParser {
  def apply(): EventParser = {
    new EventParser()
  }
}
