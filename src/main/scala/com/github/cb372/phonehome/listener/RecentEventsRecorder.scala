package com.github.cb372.phonehome.listener

import com.github.cb372.phonehome.model.{ErrorEvent, MessageEvent, TimingEvent, Received}
import scala.collection.immutable.Queue
import java.util.concurrent.atomic.AtomicReference
import scala.annotation.tailrec

/**
 * Author: chris
 * Created: 5/25/13
 */
class RecentEventsRecorder(bufferSize: Int) extends PhoneHomeEventListener {

  val recentErrors = new AtomicReference(Queue[Received[ErrorEvent]]())
  val recentMessages = new AtomicReference(Queue[Received[MessageEvent]]())
  val recentTimings = new AtomicReference(Queue[Received[TimingEvent]]())

  def onError(event: Received[ErrorEvent]) {
    update(recentErrors, { (q: Queue[Received[ErrorEvent]]) => q.enqueueFinite(event, bufferSize) })
  }

  def onMessage(event: Received[MessageEvent]) {
    update(recentMessages, { (q: Queue[Received[MessageEvent]]) => q.enqueueFinite(event, bufferSize) })
  }

  def onTiming(event: Received[TimingEvent]) {
    update(recentTimings, { (q: Queue[Received[TimingEvent]]) => q.enqueueFinite(event, bufferSize) })
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
