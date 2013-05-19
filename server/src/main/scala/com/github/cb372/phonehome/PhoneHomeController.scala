package com.github.cb372.phonehome

import model._
import listener._

import scala.util.control.Exception._

import org.slf4j.LoggerFactory
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class PhoneHomeController(listeners: Seq[PhoneHomeEventListener],
                          password: Option[String]) extends PhonehomeServerStack
                                                    with JacksonJsonSupport {
  
  implicit val jsonFormats: Formats = DefaultFormats

  private val logger =  LoggerFactory.getLogger(getClass)

  before() {
    // check password, if it is defined
    password map { p =>
      if (request.getHeader("X-PhoneHome-Auth") != p) {
        halt(403)
      }
    }
  }

  private def processEvent[E](parseResult: Either[Throwable, E])(notification: (Timestamped[E], PhoneHomeEventListener) => Unit) = {
    parseResult fold ({ e =>
      logger.info(s"Rejecting invalid json: ${request.body}")
      halt(400)
    }, { event =>
      val timestamped = Timestamped(event)
      // TODO do this asynchronously
      listeners.map(notification(timestamped, _))
      "OK"
    })
  }

  post("/errors") {
    val parseResult = catching(classOf[Exception]).either(parsedBody.extract[ErrorEvent])
    processEvent(parseResult) { case (event, listener) => listener.onError(event) }
  }

  post("/messages") {
    val parseResult = catching(classOf[Exception]).either(parsedBody.extract[MessageEvent])
    processEvent(parseResult) { case (event, listener) => listener.onMessage(event) }
  }

  post("/timings") {
    val parseResult = catching(classOf[Exception]).either(parsedBody.extract[TimingEvent])
    processEvent(parseResult) { case (event, listener) => listener.onTiming(event) }
  }

}
