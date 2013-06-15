package com.github.cb372.phonehome

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.stats.StatsRepository
import org.scalatra.json.JacksonJsonSupport
import org.json4s.{DefaultFormats, Formats}
import org.joda.time.DateTimeZone

class StatsController(statsRepository: StatsRepository) extends PhonehomeServerStack
                                                        with JacksonJsonSupport {

  private val logger =  LoggerFactory.getLogger(getClass)

  implicit val jsonFormats: Formats = DefaultFormats

  get("/errors/per/day/:days") {
    contentType = "application/json"
    statsRepository.getErrorsPerDay(params("days").toInt)
      .map { case (date, count) => List(date.toDateMidnight(DateTimeZone.UTC).getMillis, count) }
  }

  get("/") {
    contentType = "text/html"
    jade("stats")
  }
}
