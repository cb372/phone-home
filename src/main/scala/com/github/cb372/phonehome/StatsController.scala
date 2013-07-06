package com.github.cb372.phonehome

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.stats.StatsRepository
import org.scalatra.json.JacksonJsonSupport
import org.json4s.{DefaultFormats, Formats}
import org.joda.time.DateTimeZone
import scala.collection.mutable

class StatsController(statsRepository: StatsRepository) extends PhonehomeServerStack
                                                        with JacksonJsonSupport {

  private val logger =  LoggerFactory.getLogger(getClass)

  implicit val jsonFormats: Formats = DefaultFormats

  get("/errors/per/day/:days") {
    contentType = "application/json"
    statsRepository.getErrorsPerDay(params("days").toInt)
      .map { case (date, count) => List(date.toDateMidnight(DateTimeZone.UTC).getMillis, count) }
  }

  get("/errors/per/user-agent/:days") {
    contentType = "application/json"
    statsRepository.getErrorsByUserAgent(params("days").toInt)
      .toList
      .sortBy { case (name, count) => count }
      .map { case (name, count) => List(name, count) }
  }

  get("/timings/averages/per/hour/:days") {
    contentType = "application/json"
    val timings = statsRepository.getAverageTimingsPerHour(params("days").toInt)
      .map { case (ts, timing) => (ts.toDateTime.getMillis, timing) }

    val network = mutable.ListBuffer[List[Any]]()
    val requestResponse = mutable.ListBuffer[List[Any]]()
    val dom = mutable.ListBuffer[List[Any]]()
    val pageLoad = mutable.ListBuffer[List[Any]]()
    val total = mutable.ListBuffer[List[Any]]()
    for ((ts, timing) <- timings) {
      network += List(ts, timing.network)
      requestResponse += List(ts, timing.requestResponse)
      dom += List(ts, timing.dom)
      pageLoad += List(ts, timing.pageLoad)
      total += List(ts, timing.total)
    }
    Map(
      "network" -> network,
      "requestRespnse" -> requestResponse,
      "dom" -> dom,
      "pageLoad" -> pageLoad,
      "total" -> total
    )
  }

  get("/") {
    contentType = "text/html"
    jade("stats")
  }
}
