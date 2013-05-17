package com.github.cb372.phonehome

import models._

import scala.util.control.Exception._

import org.scalatra._
import scalate.ScalateSupport
import org.slf4j.{Logger, LoggerFactory}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class PhoneHomeController extends PhonehomeServerStack with JacksonJsonSupport {
  
  implicit val jsonFormats: Formats = DefaultFormats

  val logger =  LoggerFactory.getLogger(getClass)

  val listeners: Seq[ErrorListener] = Seq(
    new ErrorLogger 
  )

  before() {
    if (request.getHeader("X-PhoneHome-Auth") != "sesame") {
      halt(403)
    }
  }
  
  post("/errors") {
    val parseResult = 
      catching(classOf[Exception]).either(parsedBody.extract[ErrorPayload])     
    parseResult fold ({ e =>
      logger.info(s"Rejecting invalid json: ${request.body}")
      halt(400)
    }, { payload =>
      listeners.map(_.onError(payload))
      "OK"
    })
  }

}
