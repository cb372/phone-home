package com.github.cb372.phonehome
package listener

import model._

import org.slf4j.LoggerFactory

/**
 * Author: chris
 * Created: 5/19/13
 */
class LtsvLogger extends PhoneHomeEventListener {
  val errorsLogger =  LoggerFactory.getLogger("errors")
  val messagesLogger =  LoggerFactory.getLogger("messages")

  import DefaultLtsvFormats._

  def onError(event: Timestamped[ErrorEvent]) {
    errorsLogger.info(toLtsv(event))
  }

  def onMessage(event: Timestamped[MessageEvent]) {
    messagesLogger.info(toLtsv(event))
  }

  private def toLtsv[T: LtsvFormat](e: T): String = {
    val sb = new StringBuilder()

    // serialize to LTSV
    implicitly[LtsvFormat[T]].appendLtsv(e, sb)

    // Remove trailing tab, in order to produce valid LTSV
    if (sb.last == '\t')
      sb.deleteCharAt(sb.length - 1)

    sb.toString()
  }
}



