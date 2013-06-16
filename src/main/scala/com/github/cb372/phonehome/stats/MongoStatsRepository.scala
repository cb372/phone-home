package com.github.cb372.phonehome.stats

import com.mongodb.casbah.Imports._
import org.joda.time.{LocalDate, DateTime}
import scala.collection.mutable

/**
 * Author: chris
 * Created: 6/9/13
 */
class MongoStatsRepository(mongo: MongoDB) extends StatsRepository {

  val errors = mongo("errors")
  val messages = mongo("messages")
  val timings = mongo("timings")

  def getErrorsPerDay(days: Int) = {
    val matchQ = MongoDBObject("$match" -> ("time" $gte DateTime.now().minusDays(days).toDateMidnight.toDateTime))
    val groupQ = MongoDBObject("$group" ->
      MongoDBObject("_id" ->
        MongoDBObject(
          "year" -> MongoDBObject("$year" -> "$time"),
          "month" -> MongoDBObject("$month" -> "$time"),
          "day" -> MongoDBObject("$dayOfMonth" -> "$time")
        ),
        "count" -> MongoDBObject("$sum" -> 1)
      )
    )
    val results = errors.underlying.aggregate(matchQ, groupQ).results().iterator()

    val countsPerDay = mutable.Map[LocalDate, Int]()
    while (results.hasNext) {
      val record = results.next
      val id = record.as[DBObject]("_id")
      val date = new LocalDate(id.as[Int]("year"), id.as[Int]("month"), id.as[Int]("day"))
      countsPerDay += (date -> record.as[Int]("count"))
    }

    // return results in date order, adding zeroes for missing days
    for (n <- (days - 1).to(0, -1)) yield {
      val date = new LocalDate().minusDays(n)
      if (countsPerDay contains date)
        (date, countsPerDay(date))
      else
        (date, 0)
    }
  }

  def getErrorsByUserAgent(days: Int) = {
    val matchQ = MongoDBObject("$match" -> ("time" $gte DateTime.now().minusDays(days).toDateMidnight.toDateTime))
    val groupQ = MongoDBObject("$group" ->
      MongoDBObject(
        "_id" -> "$event.userAgent",
        "count" -> MongoDBObject("$sum" -> 1)
      )
    )
    val results = errors.underlying.aggregate(matchQ, groupQ).results().iterator()

    val countsByUserAgent = mutable.Map[String, Int]()
    while (results.hasNext) {
      val record = results.next
      countsByUserAgent += (record.as[String]("_id") -> record.as[Int]("count"))
    }
    countsByUserAgent.toMap
  }
}
