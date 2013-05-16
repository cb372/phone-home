package com.github.cb372.phonehome

import org.scalatra._
import scalate.ScalateSupport
import org.slf4j.{Logger, LoggerFactory}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class PhoneHomeController extends PhonehomeServerStack with JacksonJsonSupport {
  
  case class ErrorPayload(url: String, 
                          error: String, 
                          userAgent: String,
                          app: String) 
                          //version: Option[String], 
                          //clientAddress: Option[String],
                          //custom: Option[Map[String, String]])

  implicit val jsonFormats: Formats = DefaultFormats

  val logger =  LoggerFactory.getLogger(getClass)
  val errorsLogger =  LoggerFactory.getLogger("errors")

  before() {
    if (request.getHeader("X-PhoneHome-Auth") != "sesame") {
      halt(403)
    }
  }
  
  post("/errors") {
    try {
      val payload = parsedBody.extract[ErrorPayload]
      logger.info("payload: " + payload)
      // Log the body
      errorsLogger.info(payload.toString)
      "OK"
    } catch {
      case e: Exception => halt(400)
    }
  }

}
