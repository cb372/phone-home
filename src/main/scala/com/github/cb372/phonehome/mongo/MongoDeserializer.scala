package com.github.cb372.phonehome
package mongo

import model._

import com.mongodb.casbah.Imports._

import scala.language.implicitConversions
import org.joda.time.DateTime

/**
 * Author: chris
 * Created: 6/9/13
 */
trait MongoDeserializer[T] {
  def fromDBObject(dbObj: DBObject): T
}

object MongoDeserializer extends DefaultMongoDeserializers

private[mongo] trait DefaultMongoDeserializers {

  val unknown = "(unknown)"

  implicit def receivedDeser[T : MongoDeserializer]: MongoDeserializer[Received[T]] =
    new MongoDeserializer[Received[T]] {
      def fromDBObject(dbObj: DBObject) = {
        val id = dbObj._id map(_.toString) getOrElse Received.randomId
        val time = dbObj.getAsOrElse("time", new DateTime(1900, 1, 1, 0, 0, 0))
        val remoteHost = dbObj.getAsOrElse("remoteHost", unknown)
        val event = implicitly[MongoDeserializer[T]].fromDBObject(dbObj.getAsOrElse("event", MongoDBObject()))
        Received[T](Some(id), time, remoteHost, event)
      }
    }

  implicit object ErrorDeser extends MongoDeserializer[Error] {
    def fromDBObject(dbObj: DBObject) = {
      Error(
        dbObj.getAsOrElse("name", unknown),
        dbObj.getAsOrElse("message", unknown),
        dbObj.getAs[String]("file"),
        dbObj.getAs[String]("line")
      )
    }
  }

  implicit object ErrorEventDeser extends MongoDeserializer[ErrorEvent] {
    def fromDBObject(dbObj: DBObject) = {
      ErrorEvent(
        dbObj.getAsOrElse("app", unknown),
        dbObj.getAsOrElse("url", unknown),
        dbObj.getAsOrElse("userAgent", unknown),
        implicitly[MongoDeserializer[Error]].fromDBObject(dbObj.getAsOrElse("error", MongoDBObject())),
        dbObj.getAs[DBObject]("customFields") map implicitly[MongoDeserializer[Map[String, String]]].fromDBObject
      )
    }
  }

  implicit object TimingDeser extends MongoDeserializer[Timing] {
    def fromDBObject(dbObj: DBObject) = {
      Timing(
        dbObj.getAsOrElse("network", 0L),
        dbObj.getAsOrElse("requestResponse", 0L),
        dbObj.getAsOrElse("dom", 0L),
        dbObj.getAsOrElse("pageLoad", 0L),
        dbObj.getAsOrElse("total", 0L)
      )
    }
  }

  implicit object TimingEventDeser extends MongoDeserializer[TimingEvent] {
    def fromDBObject(dbObj: DBObject) = {
      TimingEvent(
        dbObj.getAsOrElse("app", unknown),
        dbObj.getAsOrElse("url", unknown),
        dbObj.getAsOrElse("userAgent", unknown),
        implicitly[MongoDeserializer[Timing]].fromDBObject(dbObj.getAsOrElse("timing", MongoDBObject())),
        dbObj.getAs[DBObject]("customFields") map implicitly[MongoDeserializer[Map[String, String]]].fromDBObject
      )
    }
  }

  implicit object MessageEventDeser extends MongoDeserializer[MessageEvent] {
    def fromDBObject(dbObj: DBObject) = {
      MessageEvent(
        dbObj.getAsOrElse("app", unknown),
        dbObj.getAsOrElse("url", unknown),
        dbObj.getAsOrElse("userAgent", unknown),
        dbObj.getAsOrElse("message", unknown),
        dbObj.getAs[DBObject]("customFields") map implicitly[MongoDeserializer[Map[String, String]]].fromDBObject
      )
    }
  }

  implicit object StringMapDeser extends MongoDeserializer[Map[String, String]] {
    def fromDBObject(dbObj: DBObject) = {
      (dbObj.keys map { k =>
        k -> dbObj.as[String](k)
      }).toMap
    }
  }
}
