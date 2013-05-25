package com.github.cb372.phonehome.listener

import com.github.cb372.phonehome.model.{ErrorEvent, MessageEvent, TimingEvent, Timestamped}
import scala.collection.immutable.Queue

/**
 * Author: chris
 * Created: 5/25/13
 */
class RecentEventsRecorder(bufferSize: Int) extends PhoneHomeEventListener {

  var recentErrors = Queue[Timestamped[ErrorEvent]]()
  var recentMessages = Queue[Timestamped[MessageEvent]]()
  var recentTimings = Queue[Timestamped[TimingEvent]]()

  def onError(event: Timestamped[ErrorEvent]) {
    recentErrors = recentErrors.enqueueFinite(event, bufferSize)
  }

  def onMessage(event: Timestamped[MessageEvent]) {
    recentMessages = recentMessages.enqueueFinite(event, bufferSize)
  }

  def onTiming(event: Timestamped[TimingEvent]) {
    recentTimings = recentTimings.enqueueFinite(event, bufferSize)
  }

  /**
   * Extend Queue with an enqueueFinite() method
   * that drops old items from the queue if it is too long
   */
  implicit class FiniteQueue[A](q: Queue[A]) {
    def enqueueFinite[B >: A](elem: B, maxSize: Int): Queue[B] = {
      var ret = q.enqueue(elem)
      while (ret.size > maxSize) { ret = ret.dequeue._2 }
      ret
    }
  }
}
