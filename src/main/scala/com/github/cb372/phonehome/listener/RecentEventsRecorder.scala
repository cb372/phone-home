package com.github.cb372.phonehome.listener

import com.github.cb372.phonehome.model.{ErrorEvent, MessageEvent, TimingEvent, Timestamped}
import scala.collection.immutable.Queue
import java.util.concurrent.atomic.AtomicReference
import scala.annotation.tailrec

/**
 * Author: chris
 * Created: 5/25/13
 */
class RecentEventsRecorder(bufferSize: Int) extends PhoneHomeEventListener {

  val recentErrors = new AtomicReference(Queue[Timestamped[ErrorEvent]]())
  val recentMessages = new AtomicReference(Queue[Timestamped[MessageEvent]]())
  val recentTimings = new AtomicReference(Queue[Timestamped[TimingEvent]]())

  def onError(event: Timestamped[ErrorEvent]) {
    update(recentErrors, { (q: Queue[Timestamped[ErrorEvent]]) => q.enqueueFinite(event, bufferSize) })
  }

  def onMessage(event: Timestamped[MessageEvent]) {
    update(recentMessages, { (q: Queue[Timestamped[MessageEvent]]) => q.enqueueFinite(event, bufferSize) })
  }

  def onTiming(event: Timestamped[TimingEvent]) {
    update(recentTimings, { (q: Queue[Timestamped[TimingEvent]]) => q.enqueueFinite(event, bufferSize) })
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

  @tailrec
  private def update[T](ref: AtomicReference[T], f: T => T) {
    val before = ref.get()
    val after = f(before)
    if (!ref.compareAndSet(before, after)) {
      update(ref, f)
    }
  }
}
