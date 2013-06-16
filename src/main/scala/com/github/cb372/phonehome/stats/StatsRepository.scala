package com.github.cb372.phonehome.stats

import org.joda.time.{LocalDate, DateMidnight}

/**
 * Author: chris
 * Created: 6/9/13
 */
trait StatsRepository {

  def getErrorsPerDay(days: Int): Seq[(LocalDate, Int)]

  def getErrorsByUserAgent(days: Int): Map[String, Int]


}
