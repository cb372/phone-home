package com.github.cb372.phonehome.model

import org.joda.time.DateTime
import java.util.concurrent.atomic.AtomicLong

case class Error(name: String,
                 message: String,
                 file: Option[String],
                 line: Option[String]) {

  val locationInfo: Option[String] = {
    for {
      f <- file
      l <- line
    } yield s" @ $f:$l"
  }

  override def toString: String = {
    s"""$name: $message ${locationInfo getOrElse ""}"""
  }
}

case class Timing(network: Long,
                  requestResponse: Long,
                  dom: Long,
                  pageLoad: Long,
                  total: Long)

abstract class Event(val app: String,
                     val url: String,
                     val userAgent: String,
                     val customFields: Option[Map[String, String]])

case class ErrorEvent(override val app: String,
                      override val url: String,
                      override val userAgent: String,
                      error: Error,
                      override val customFields: Option[Map[String, String]]) extends Event(app, url, userAgent, customFields)

case class MessageEvent(override val app: String,
                        override val url: String,
                        override val userAgent: String,
                        message: String,
                        override val customFields: Option[Map[String, String]]) extends Event(app, url, userAgent, customFields)

case class TimingEvent(override val app: String,
                       override val url: String,
                       override val userAgent: String,
                       timing: Timing,
                       override val customFields: Option[Map[String, String]]) extends Event(app, url, userAgent, customFields)

case class Received[+T](id: Option[String], time: DateTime, remoteHost: String, event: T)

object Received {
  def randomId = List.fill(10)(util.Random.nextPrintableChar()).mkString

  def apply[T](remoteHost: String, event: T): Received[T] =
    Received(Some(randomId), new DateTime(), remoteHost, event)
}
