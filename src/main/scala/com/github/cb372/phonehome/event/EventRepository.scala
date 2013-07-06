package com.github.cb372.phonehome
package event

import model._

/**
 * Author: chris
 * Created: 7/6/13
 */
trait EventRepository {

  def latestErrors(limit: Int = 100): Seq[Received[ErrorEvent]]
  def latestMessages(limit: Int = 100): Seq[Received[MessageEvent]]
  def latestTimings(limit: Int = 100): Seq[Received[TimingEvent]]

}
