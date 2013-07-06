package com.github.cb372.phonehome.mongo

import org.scalatra.test.scalatest.ScalatraFlatSpec
import com.github.cb372.phonehome.PhoneHomeController
import com.github.cb372.phonehome.listener.PhoneHomeEventListener
import com.github.cb372.phonehome.model._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext
import org.joda.time.DateTime
import scala.Some
import com.github.cb372.phonehome.model.MessageEvent
import com.github.cb372.phonehome.model.TimingEvent
import com.github.cb372.phonehome.model.ErrorEvent

/**
 * Author: chris
 * Created: 7/6/13
 */
class PhoneHomeControllerSpec extends ScalatraFlatSpec {
  val myListener = new MyListener

  addServlet(new PhoneHomeController(Seq(myListener), None)(new SameThreadExecutionContext), "/ph/*")

  behavior of "PhoneHomeController"

  it should "pass received error events to the listener" in {
    val json = buildJson(
      """
        |  "error": {
        |     "name": "SyntaxError",
        |     "message": "Miaow!",
        |     "file": "http://static.foo.com/qwerty.js",
        |     "line": 123
        |  }
      """.stripMargin)

    doPost("/ph/errors", json)

    myListener.errors should have size 1
    val received = myListener.errors(0)
    checkCommonFields(received)
    received.event.error.name should be ("SyntaxError")
    received.event.error.message should be ("Miaow!")
    received.event.error.file should be (Some("http://static.foo.com/qwerty.js"))
    received.event.error.line should be (Some("123"))
  }

  it should "pass received timing events to the listener" in {
    val json = buildJson(
      """
        |  "timing": {
        |     "network": 100,
        |     "requestResponse": 200,
        |     "dom": 300,
        |     "pageLoad": 400,
        |     "total": 500
        |  }
      """.stripMargin)

    doPost("/ph/timings", json)

    myListener.timings should have size 1
    val received = myListener.timings(0)
    checkCommonFields(received)
    received.event.timing.network should be (100L)
    received.event.timing.requestResponse should be (200L)
    received.event.timing.dom should be (300L)
    received.event.timing.pageLoad should be (400L)
    received.event.timing.total should be (500L)
  }

  it should "pass received message events to the listener" in {
    val json = buildJson(
      """
        |  "message": "Howdy!"
      """.stripMargin)

    doPost("/ph/messages", json)

    myListener.messages should have size 1
    val received = myListener.messages(0)
    checkCommonFields(received)
    received.event.message should be("Howdy!")
  }

  def checkCommonFields(received: Received[Event]) {
    received.time should be > DateTime.now().minusMinutes(1)
    received.event.app should be("my app")
    received.event.url should be("http://www.foo.com/bar/baz.html")
    received.event.userAgent should be("Firefox 99.3")
    received.event.customFields should be(Some(Map("abc" -> "def", "ghi" -> "jkl")))
  }

  def buildJson(extraField: String) = {
    s"""
      |{
      |  "app": "my app",
      |  "url": "http://www.foo.com/bar/baz.html",
      |  "userAgent": "Firefox 99.3",
      |  "customFields": { "abc": "def", "ghi": "jkl" },
      |${extraField}
      |}
    """.stripMargin
  }

  /**
   * Send a POST to the server and check the response
   */
  def doPost(url: String, json: String) {
    post(url, body = json.getBytes("UTF-8"), headers = Map("Content-Type" -> "application/json")) {
      status should be(200)
      body should include("OK")
    }
  }

  implicit val dateTimeOrdering = new Ordering[DateTime]{
    def compare(x: DateTime, y: DateTime) =
      if (x.isBefore(y)) -1
      else if (y.isBefore(x)) 1
      else 0
  }

  /*
   * Make listener run in same thread as test, to avoiding unpredictability due to timing
   */
  class SameThreadExecutionContext extends ExecutionContext {
    def execute(runnable: Runnable) { runnable.run() }
    def reportFailure(t: Throwable) { throw t }
  }

  class MyListener extends PhoneHomeEventListener {
    val errors = ArrayBuffer[Received[ErrorEvent]]()
    val messages = ArrayBuffer[Received[MessageEvent]]()
    val timings = ArrayBuffer[Received[TimingEvent]]()

    def onError(event: Received[ErrorEvent]) { errors += event }

    def onMessage(event: Received[MessageEvent]) { messages += event }

    def onTiming(event: Received[TimingEvent]) { timings += event }
  }

}
