package com.github.cb372.phonehome

class HealthCheckController extends PhonehomeServerStack {

  get("/") {
    contentType = "text/plain"
    // Nothing to check!
    "OK"
  }

}
