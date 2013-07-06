package com.github.cb372.phonehome
package stats

import model.Timing

import org.joda.time.{DateTime, LocalDate}

/**
 * Author: chris
 * Created: 6/9/13
 */
trait StatsRepository {

  def getErrorsPerDay(days: Int): Seq[(LocalDate, Int)]

  def getErrorsByUserAgent(days: Int): Map[String, Int]

  def getAverageTimingsPerHour(days: Int): Seq[(DateTime, Timing)]

}
