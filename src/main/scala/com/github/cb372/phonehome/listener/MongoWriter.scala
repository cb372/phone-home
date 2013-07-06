package com.github.cb372.phonehome
package listener

import model._
import com.mongodb.casbah.Imports._

import org.slf4j.LoggerFactory
import com.github.cb372.phonehome.mongo.MongoSerializer

/**
 * Author: chris
 * Created: 6/9/13
 */
class MongoWriter(mongo: MongoDB) extends PhoneHomeEventListener {
  private val logger =  LoggerFactory.getLogger(getClass)

  val errors = mongo("errors")
  val messages = mongo("messages")
  val timings = mongo("timings")

  def onError(event: Received[ErrorEvent]) {
    errors.insert(implicitly[MongoSerializer[Received[ErrorEvent]]].toDBObject(event))
  }

  def onMessage(event: Received[MessageEvent]) {
    messages.insert(implicitly[MongoSerializer[Received[MessageEvent]]].toDBObject(event))
  }

  def onTiming(event: Received[TimingEvent]) {
    timings.insert(implicitly[MongoSerializer[Received[TimingEvent]]].toDBObject(event))
  }

}

