package com.github.cb372.phonehome

import model._
import listener._

import org.slf4j.LoggerFactory

class RecentEventsController(recorder: RecentEventsRecorder) extends PhonehomeServerStack {

  private val logger =  LoggerFactory.getLogger(getClass)

  before() {
    contentType="text/html"
  }

  get("/") {
    jade("recent-events",
      "errors" -> recorder.recentErrors.get,
      "timings" -> recorder.recentTimings.get,
      "messages" -> recorder.recentMessages.get)
  }

}
