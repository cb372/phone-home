package com.github.cb372.phonehome

import org.scalatra._
import scalate.ScalateSupport
import org.slf4j.{Logger, LoggerFactory}

class PhoneHomeServlet extends PhonehomeServerStack {

  val logger =  LoggerFactory.getLogger(getClass)
  val errorsLogger =  LoggerFactory.getLogger("errors")

  before() {
    if (request.getHeader("X-PhoneHome-Auth") != "sesame") {
      halt(403)
    }
  }
  
  post("/errors") {
    logger.info("params: " + params)
    // Log the body
    errorsLogger.info(request.body)
    "OK"
  }

}
