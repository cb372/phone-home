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

  import DefaultLtsvFormats._

  def onError(event: Timestamped[ErrorEvent]) {
    errorsLogger.info(toLtsv(event))
  }

  private def toLtsv[T: LtsvFormat](e: T): String = {
    val sb = new StringBuilder()
    implicitly[LtsvFormat[T]].appendLtsv(e, sb)
    sb.toString()
  }
}



