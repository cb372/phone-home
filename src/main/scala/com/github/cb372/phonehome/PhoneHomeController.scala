package com.github.cb372.phonehome

import model._
import listener._

import scala.util.control.Exception._

import org.slf4j.LoggerFactory
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.scalatra.{ScalatraServlet, CorsSupport}
import scala.concurrent.{future, ExecutionContext}

class PhoneHomeController(listeners: Seq[PhoneHomeEventListener],
                          authString: Option[String])
                         (implicit ec: ExecutionContext)
                                                    extends ScalatraServlet
                                                    with JacksonJsonSupport
                                                    with CorsSupport {

  implicit val jsonFormats: Formats = DefaultFormats

  private val logger =  LoggerFactory.getLogger(getClass)

  before() {
    // check auth string, if it is defined
    if (request.getMethod != "OPTIONS") {
      authString map { a =>
        if (request.getHeader("X-PhoneHome-Auth") != a) {
          halt(403)
        }
      }
    }

    response.setHeader("Access-Control-Allow-Origin:", "*")
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  private def processEvent[E](parseResult: Either[Throwable, E])(notification: (Timestamped[E], PhoneHomeEventListener) => Unit) = {
    parseResult fold ({ e =>
      logger.info(s"Rejecting invalid json: ${request.body}")
      halt(400)
    }, { event =>
      val timestamped = Timestamped(event)
      // notify listeners asynchronously
      listeners.map { l => future { notification(timestamped, l) } }
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
