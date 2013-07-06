package com.github.cb372.phonehome

import com.github.cb372.phonehome.event.EventRepository

class RecentEventsController(repo: EventRepository) extends PhonehomeServerStack {

  before() {
    contentType="text/html"
  }

  get("/") {
    jade("recent-events",
      "errors" -> repo.latestErrors(),
      "timings" -> repo.latestTimings(),
      "messages" -> repo.latestMessages()
    )
  }

}
