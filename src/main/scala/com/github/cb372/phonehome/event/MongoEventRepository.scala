package com.github.cb372.phonehome
package event

import model._
import mongo.MongoDeserializer

import com.mongodb.casbah.{MongoCollection, MongoDB}
import com.mongodb.casbah.commons.MongoDBObject

/**
 * Author: chris
 * Created: 7/6/13
 */
class MongoEventRepository(mongo: MongoDB) extends EventRepository {

  val errors = mongo("errors")
  val messages = mongo("messages")
  val timings = mongo("timings")

  def latestErrors(limit: Int) = latestEvents[ErrorEvent](errors, limit)

  def latestMessages(limit: Int) = latestEvents[MessageEvent](messages, limit)

  def latestTimings(limit: Int) = latestEvents[TimingEvent](timings, limit)

  private def latestEvents[T](collection: MongoCollection, limit: Int)(implicit deser: MongoDeserializer[Received[T]]) =
    collection.find()
      .sort(MongoDBObject("_id" -> -1))
      .limit(limit)
      .map(deser.fromDBObject)
      .toSeq

}
