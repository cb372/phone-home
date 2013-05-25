package com.github.cb372.phonehome
package listener

import model._

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.ltsv.{LtsvBuilder, DefaultLtsvFormats, LtsvFormat}

/**
 * Author: chris
 * Created: 5/19/13
 */
class LtsvPhoneHomeLogger extends PhoneHomeEventListener with LtsvBuilder {
  val errorsLogger =  LoggerFactory.getLogger("errors")
  val messagesLogger =  LoggerFactory.getLogger("messages")
  val timingsLogger =  LoggerFactory.getLogger("timings")

  import DefaultLtsvFormats._

  def onError(event: Timestamped[ErrorEvent]) {
    errorsLogger.info(toLtsv(event))
  }

  def onMessage(event: Timestamped[MessageEvent]) {
    messagesLogger.info(toLtsv(event))
  }

  def onTiming(event: Timestamped[TimingEvent]) {
    timingsLogger.info(toLtsv(event))
  }

}



