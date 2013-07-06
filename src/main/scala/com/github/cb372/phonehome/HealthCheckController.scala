package com.github.cb372.phonehome

import org.scalatra.ScalatraServlet

class HealthCheckController extends ScalatraServlet {

  get("/") {
    contentType = "text/plain"
    // Nothing to check!
    "OK"
  }

}
