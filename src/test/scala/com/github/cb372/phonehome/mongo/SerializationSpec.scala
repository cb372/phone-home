package com.github.cb372.phonehome.mongo

import org.scalatest.FlatSpec
import com.github.cb372.phonehome.model._
import org.joda.time.DateTime
import org.scalatest.matchers.ShouldMatchers
import org.bson.types.ObjectId

/**
 * Author: chris
 * Created: 7/6/13
 */
class SerializationSpec extends FlatSpec with ShouldMatchers {
  behavior of "mongodb serialization"

  it should "round-trip a received ErrorEvent" in {
    checkRoundTrip(
      Received(
        None, new DateTime(), "1.2.3.4",
        ErrorEvent(
          "my app", "http://foo.com/bar.html",
          "IE 10",
          Error(
            "TypeError",
            "Oops!",
            Some("script.js"),
            None
          ),
          Some(Map("a" -> "A", "b" -> "B"))
        )
      )
    )
  }

  it should "round-trip a received MessageEvent" in {
    checkRoundTrip(
      Received(
        None, new DateTime(), "1.2.3.4",
        MessageEvent(
          "my app", "http://foo.com/bar.html",
          "IE 10",
          "my message",
          Some(Map("a" -> "A", "b" -> "B"))
        )
      )
    )
  }

  it should "round-trip a received TimingEvent" in {
    checkRoundTrip(
      Received(
        None, new DateTime(), "1.2.3.4",
        TimingEvent(
          "my app", "http://foo.com/bar.html",
          "IE 10",
          Timing(1L, 2L, 3L, 4L, 5L),
          Some(Map("a" -> "A", "b" -> "B"))
        )
      )
    )
  }

  def checkRoundTrip[T : MongoSerializer : MongoDeserializer](original: Received[T]) {
    val objectId = ObjectId.get()
    val originalWithObjectId = original.copy(id = Some(objectId.toString))

    val serialized = MongoSerializer.receivedSer[T].toDBObject(original)
    serialized.put("_id", objectId)

    val deserialized = MongoDeserializer.receivedDeser[T].fromDBObject(serialized)

    deserialized should equal (originalWithObjectId)
  }

}
