package com.github.cb372.phonehome
package listener

import model._

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.ltsv.LtsvBuilder

/**
 * Author: chris
 * Created: 5/19/13
 */
class LtsvPhoneHomeLogger extends PhoneHomeEventListener with LtsvBuilder {
  val errorsLogger =  LoggerFactory.getLogger("errors")
  val messagesLogger =  LoggerFactory.getLogger("messages")
  val timingsLogger =  LoggerFactory.getLogger("timings")

  def onError(event: Received[ErrorEvent]) {
    errorsLogger.info(toLtsv(event))
  }

  def onMessage(event: Received[MessageEvent]) {
    messagesLogger.info(toLtsv(event))
  }

  def onTiming(event: Received[TimingEvent]) {
    timingsLogger.info(toLtsv(event))
  }

}



