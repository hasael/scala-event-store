package eventstore

import eventstore.events.{PaymentAccepted, PaymentData, PaymentDeclined, PaymentPending}
import eventstore.parsers.EventParser
import org.scalatest.FunSuite
import org.scalatest.Matchers._

import scala.util.{Success, Try}

class EventParserTest extends FunSuite {

  test("Parse PaymentAccepted") {
    val message = "{\n  \"eventType\":\"PaymentAccepted\",\n  \"transactionId\": \"guid\",\n  \"amount\": 3.2,\n  \"userId\" : \"userIdValue\",\n  \"currency\": \"USD\",\n  \"paymentData\": {\n    \"paymentType\": \"paymentTypeValue\",\n    \"pspId\": \"pspIdValue\",\n    \"userAccountId\": 0,\n    \"cardId\": \"cardGuid\"\n  },\n  \"transactionTime\": \"2020-03-02T18:12:17.523Z\"\n}"
    val actual = EventParser.parseEvent(message)
    val expected: Try[PaymentAccepted] = Success(PaymentAccepted("guid", 3.2, "userIdValue", "USD", PaymentData("paymentTypeValue", "pspIdValue", 0, "cardGuid"), "2020-03-02T18:12:17.523Z"))
    actual shouldBe expected
  }

  test("Parse PaymentDeclined") {
    val message = "{\n  \"eventType\":\"PaymentDeclined\",\n  \"transactionId\": \"guid\",\n  \"amount\": 3.2,\n  \"userId\" : \"userIdValue\",\n  \"currency\": \"USD\", \"reason\" : \"reasonValue\",\n  \"paymentData\": {\n    \"paymentType\": \"paymentTypeValue\",\n    \"pspId\": \"pspIdValue\",\n    \"userAccountId\": 0,\n    \"cardId\": \"cardGuid\"\n  },\n  \"transactionTime\": \"2020-03-02T18:12:17.523Z\"\n}"
    val actual = EventParser.parseEvent(message)
    val expected: Try[PaymentDeclined] = Success(PaymentDeclined("guid", 3.2, "userIdValue", "USD", "reasonValue", PaymentData("paymentTypeValue", "pspIdValue", 0, "cardGuid"), "2020-03-02T18:12:17.523Z"))
    actual shouldBe expected
  }

  test("Parse PaymentPending") {
    val message = "{\n  \"eventType\":\"PaymentPending\",\n  \"transactionId\": \"guid\",\n  \"userId\" : \"userIdValue\",\n  \"amount\": 3.2,\n  \"currency\": \"USD\",\n  \"paymentType\" : \"paymentTypeValue\",\n  \"transactionTime\": \"2020-03-02T18:12:17.523Z\"\n}"
    val actual = EventParser.parseEvent(message)
    val expected: Try[PaymentPending] = Success(PaymentPending("guid", 3.2, "userIdValue", "USD", "paymentTypeValue", "2020-03-02T18:12:17.523Z"))
    actual shouldBe expected
  }
}
