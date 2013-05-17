package com.github.cb372.phonehome

import models._
import org.slf4j.{Logger, LoggerFactory}

trait ErrorListener {
  def onError(payload: ErrorPayload)
}

class ErrorLogger extends ErrorListener {
  val errorsLogger =  LoggerFactory.getLogger("errors")

  def onError(payload: ErrorPayload) {
    errorsLogger.info(toLtsv(payload))
  }

  private def toLtsv(e: ErrorPayload): String = {
    val sb = new StringBuilder()
    sb.append(s"app:${e.app}\turl:${e.url}\terror:${e.error}\tuserAgent:${e.userAgent}")
    for { m <- e.customFields; k <- m.keys } sb.append(s"\t${k}:${m(k)}") 
    sb.toString
  }
}
