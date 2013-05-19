package com.github.cb372.phonehome.model

import org.joda.time.DateTime

case class Error(name: String,
                 message: String,
                 file: Option[String],
                 line: Option[String])

case class ErrorEvent(app: String,
                        url: String, 
                        userAgent: String,
                        error: Error,
                        customFields: Option[Map[String, String]])

case class Timestamped[T](time: DateTime, event: T)

object Timestamped {
  def apply[T](event: T): Timestamped[T] = Timestamped(new DateTime(), event)
}
