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

object DefaultMongoSerializers {

  implicit def receivedSer[T : MongoSerializer](received: Received[T]): MongoSerializer[Received[T]] = new MongoSerializer[Received[T]] {
    def toDBObject(t: Received[T]) =
      MongoDBObject(
        // don't bother serializing the ID, as Mongo provides a better one for us
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
        "customFields" -> event.customFields,
        "message" -> event.message
      )
    }
  }
}
