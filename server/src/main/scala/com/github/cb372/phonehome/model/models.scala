package com.github.cb372.phonehome.model

import org.joda.time.DateTime

case class Error(name: String,
                 message: String,
                 file: Option[String],
                 line: Option[String])

case class Timing(network: Long,
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

case class Timestamped[T](time: DateTime, event: T)

object Timestamped {
  def apply[T](event: T): Timestamped[T] = Timestamped(new DateTime(), event)
}
