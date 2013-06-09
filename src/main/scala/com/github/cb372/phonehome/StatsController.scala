package com.github.cb372.phonehome

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.stats.StatsRepository

class StatsController(statsRepository: StatsRepository) extends PhonehomeServerStack {

  private val logger =  LoggerFactory.getLogger(getClass)

  get("/") {
    contentType="application/json"
    statsRepository.getErrorsPerDay(7)
  }

}
