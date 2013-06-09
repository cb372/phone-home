package com.github.cb372.phonehome.stats

import org.joda.time.DateMidnight

/**
 * Author: chris
 * Created: 6/9/13
 */
trait StatsRepository {

  def getErrorsPerDay(days: Int): Map[DateMidnight, Int]

}
