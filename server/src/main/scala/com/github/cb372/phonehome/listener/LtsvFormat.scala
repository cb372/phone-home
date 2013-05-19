package com.github.cb372.phonehome
package listener

import model._

/**
 * Author: chris
 * Created: 5/19/13
 */
trait LtsvFormat[T] {
  def appendLtsv(t: T, sb: StringBuilder): Unit
}

object DefaultLtsvFormats {
  implicit def timestampedLtsv[T: LtsvFormat]: LtsvFormat[Timestamped[T]] = new LtsvFormat[Timestamped[T]] {
    def appendLtsv(t: Timestamped[T], sb: StringBuilder) {
      sb.append(s"time:${t.time}\t")
      implicitly[LtsvFormat[T]].appendLtsv(t.event, sb)
    }
  }

  implicit object ErrorEventLtsv extends LtsvFormat[ErrorEvent] {
    def appendLtsv(e: ErrorEvent, sb: StringBuilder) {
      sb.append(s"app:${e.app}\turl:${e.url}\tuserAgent:${e.userAgent}\t")
      ErrorLtsv.appendLtsv(e.error, sb)
      for { m <- e.customFields; k <- m.keys } sb.append(s"$k:${m(k)}\t")
    }
  }

  implicit object ErrorLtsv extends LtsvFormat[Error] {
    def appendLtsv(e: Error, sb: StringBuilder) {
      sb.append(s"errorName:${e.name}\terrorMessage:${e.message}\t")
      for { file <- e.file } sb.append(s"errorFile:${file}\t")
      for { line <- e.line } sb.append(s"errorLine:${line}\t")
    }
  }

}

