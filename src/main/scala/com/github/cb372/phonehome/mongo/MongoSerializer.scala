package com.github.cb372.phonehome
package mongo

import model._

import com.mongodb.casbah.Imports._

import scala.language.implicitConversions

/**
 * Author: chris
 * Created: 6/9/13
 */
trait MongoSerializer[T] {
  def toDBObject(t: T): DBObject
}

object MongoSerializer extends DefaultMongoSerializers

private[mongo] trait DefaultMongoSerializers {

  import com.mongodb.casbah.commons.conversions.scala._
  RegisterJodaTimeConversionHelpers()

  implicit def receivedSer[T : MongoSerializer]: MongoSerializer[Received[T]] =
    new MongoSerializer[Received[T]] {
      def toDBObject(received: Received[T]) =
        MongoDBObject(
          "time" -> received.time,
          "remoteHost" -> received.remoteHost,
          "event" -> implicitly[MongoSerializer[T]].toDBObject(received.event)
        )
    }

  implicit object ErrorSer extends MongoSerializer[Error] {
    def toDBObject(error: Error) = {
      MongoDBObject(
        "name" -> error.name,
        "message" -> error.message,
        "file" -> error.file,
        "line" -> error.line
      )
    }
  }

  implicit object ErrorEventSer extends MongoSerializer[ErrorEvent] {
    def toDBObject(event: ErrorEvent) = {
      MongoDBObject(
        "app" -> event.app,
        "url" -> event.url,
        "userAgent" -> event.userAgent,
        "customFields" -> event.customFields,
        "error" -> implicitly[MongoSerializer[Error]].toDBObject(event.error)
      )
    }
  }

  implicit object TimingSer extends MongoSerializer[Timing] {
    def toDBObject(timing: Timing) = {
      MongoDBObject(
        "network" -> timing.network,
        "requestResponse" -> timing.requestResponse,
        "dom" ->  timing.dom,
        "pageLoad" ->  timing.pageLoad,
        "total" ->  timing.total
      )
    }
  }

  implicit object TimingEventSer extends MongoSerializer[TimingEvent] {
    def toDBObject(event: TimingEvent) = {
      MongoDBObject(
        "app" -> event.app,
        "url" -> event.url,
        "userAgent" -> event.userAgent,
        "customFields" -> event.customFields,
        "timing" -> implicitly[MongoSerializer[Timing]].toDBObject(event.timing)
      )
    }
  }

  implicit object MessageEventSer extends MongoSerializer[MessageEvent] {
    def toDBObject(event: MessageEvent) = {
      MongoDBObject(
        "app" -> event.app,
        "url" -> event.url,
        "userAgent" -> event.userAgent,
        "customFields" -> event.customFields,
        "message" -> event.message
      )
    }
  }
}
