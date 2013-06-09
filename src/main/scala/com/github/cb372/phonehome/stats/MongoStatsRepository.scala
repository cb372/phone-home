package com.github.cb372.phonehome.stats

import com.mongodb.casbah.Imports._
import org.joda.time.DateTime

/**
 * Author: chris
 * Created: 6/9/13
 */
class MongoStatsRepository(mongo: MongoDB) extends StatsRepository {

  val errors = mongo("errors")
  val messages = mongo("messages")
  val timings = mongo("timings")

  def getErrorsPerDay(days: Int) = {
    errors.filter(_.containsField("time"))
          .filter(_.getAs[DateTime]("time").get.isAfter(DateTime.now().minusDays(days).toDateMidnight))
          .groupBy(_.getAs[DateTime]("time").get.toDateMidnight)
          .map { case (date, objs) => (date, objs.size) }
          .toMap
  }
}
