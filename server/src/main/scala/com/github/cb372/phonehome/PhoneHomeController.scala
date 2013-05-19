package com.github.cb372.phonehome

import model._
import listener._

import scala.util.control.Exception._

import org.slf4j.LoggerFactory
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class PhoneHomeController(listeners: Seq[PhoneHomeEventListener]) extends PhonehomeServerStack with JacksonJsonSupport {
  
  implicit val jsonFormats: Formats = DefaultFormats

  val logger =  LoggerFactory.getLogger(getClass)

  before() {
    if (request.getHeader("X-PhoneHome-Auth") != "sesame") {
      halt(403)
    }
  }
  
  post("/errors") {
    val parseResult = catching(classOf[Exception]).either(parsedBody.extract[ErrorEvent])
    parseResult fold ({ e =>
      logger.info(s"Rejecting invalid json: ${request.body}")
      halt(400)
    }, { event =>
      // TODO do this asynchronously
      listeners.map(_.onError(Timestamped(event)))
      "OK"
    })
  }

  post("/messages") {
    val parseResult = catching(classOf[Exception]).either(parsedBody.extract[MessageEvent])
    parseResult fold ({ e =>
      logger.info(s"Rejecting invalid json: ${request.body}")
      halt(400)
    }, { event =>
    // TODO do this asynchronously
      listeners.map(_.onMessage(Timestamped(event)))
      "OK"
    })
  }

}
