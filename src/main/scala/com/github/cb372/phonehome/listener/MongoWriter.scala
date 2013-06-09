package com.github.cb372.phonehome.listener

import com.github.cb372.phonehome.model.{ErrorEvent, MessageEvent, TimingEvent, Received}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.mongo.DefaultMongoSerializers

/**
 * Author: chris
 * Created: 6/9/13
 */
class MongoWriter(mongo: MongoDB) extends PhoneHomeEventListener {
  private val logger =  LoggerFactory.getLogger(getClass)

  import com.mongodb.casbah.commons.conversions.scala._
  RegisterJodaTimeConversionHelpers()

  val errors = mongo("errors")
  val messages = mongo("messages")
  val timings = mongo("timings")

  import DefaultMongoSerializers._

  def onError(event: Received[ErrorEvent]) {
    val dbObj: DBObject = DefaultMongoSerializers.receivedSer(event).toDBObject(event)
    errors.save(dbObj)
  }

  def onMessage(event: Received[MessageEvent]) {
    val dbObj: DBObject = DefaultMongoSerializers.receivedSer(event).toDBObject(event)
    messages.insert(dbObj)
  }

  def onTiming(event: Received[TimingEvent]) {
    val dbObj: DBObject = DefaultMongoSerializers.receivedSer(event).toDBObject(event)
    timings.insert(dbObj)
  }

}

