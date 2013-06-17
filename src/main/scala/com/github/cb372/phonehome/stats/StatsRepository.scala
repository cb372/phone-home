package com.github.cb372.phonehome.stats

import org.joda.time.{DateTime, LocalDate}
import com.github.cb372.phonehome.model.Timing

/**
 * Author: chris
 * Created: 6/9/13
 */
trait StatsRepository {

  def getErrorsPerDay(days: Int): Seq[(LocalDate, Int)]

  def getErrorsByUserAgent(days: Int): Map[String, Int]

  def getAverageTimingsPerHour(days: Int): Seq[(DateTime, Timing)]

}
