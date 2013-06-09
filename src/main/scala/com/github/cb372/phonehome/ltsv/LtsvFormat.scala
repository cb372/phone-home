package com.github.cb372.phonehome
package ltsv

import model._

/**
 * Author: chris
 * Created: 5/19/13
 */
trait LtsvFormat[T] {
  def appendLtsv(t: T, sb: StringBuilder): Unit
}

object DefaultLtsvFormats {
  private def appendFixedFields(e: Event, sb: StringBuilder) {
    sb.append(s"app:${e.app}\turl:${e.url}\tuserAgent:${e.userAgent}\t")
  }

  private def appendCustomFields(e: Event, sb: StringBuilder) {
    for { m <- e.customFields; k <- m.keys } sb.append(s"$k:${m(k)}\t")
  }

  implicit def receivedLtsv[T: LtsvFormat]: LtsvFormat[Received[T]] = new LtsvFormat[Received[T]] {
    def appendLtsv(r: Received[T], sb: StringBuilder) {
      sb.append(s"time:${r.time}\thost:${r.remoteHost}\t")
      implicitly[LtsvFormat[T]].appendLtsv(r.event, sb)
    }
  }

  implicit object ErrorLtsv extends LtsvFormat[Error] {
    def appendLtsv(e: Error, sb: StringBuilder) {
      sb.append(s"errorName:${e.name}\terrorMessage:${e.message}\t")
      for { file <- e.file } sb.append(s"errorFile:$file\t")
      for { line <- e.line } sb.append(s"errorLine:$line\t")
    }
  }

  implicit object ErrorEventLtsv extends LtsvFormat[ErrorEvent] {
    def appendLtsv(e: ErrorEvent, sb: StringBuilder) {
      appendFixedFields(e, sb)
      implicitly[LtsvFormat[Error]].appendLtsv(e.error, sb)
      appendCustomFields(e, sb)
    }
  }

  implicit object MessageEventLtsv extends LtsvFormat[MessageEvent] {
    def appendLtsv(e: MessageEvent, sb: StringBuilder) {
      appendFixedFields(e, sb)
      sb.append(s"message:${e.message}\t")
      appendCustomFields(e, sb)
    }
  }

  implicit object TimingLtsv extends LtsvFormat[Timing] {
    def appendLtsv(t: Timing, sb: StringBuilder) {
      sb.append(s"networkTime:${t.network}\trequestResponseTime:${t.requestResponse}\tdomTime:${t.dom}\tpageLoadTime:${t.pageLoad}\ttotalTime:${t.total}\t")
    }
  }

  implicit object TimingEventLtsv extends LtsvFormat[TimingEvent] {
    def appendLtsv(e: TimingEvent, sb: StringBuilder) {
      appendFixedFields(e, sb)
      implicitly[LtsvFormat[Timing]].appendLtsv(e.timing, sb)
      appendCustomFields(e, sb)
    }
  }

}

