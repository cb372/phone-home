package com.github.cb372.phonehome

class RootController extends PhonehomeServerStack {

  get("/") {
    redirect("/stats")
  }

}
