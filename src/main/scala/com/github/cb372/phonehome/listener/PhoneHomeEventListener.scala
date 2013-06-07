package com.github.cb372.phonehome
package listener

import model._

trait PhoneHomeEventListener {
  def onError(event: Received[ErrorEvent])
  def onMessage(event: Received[MessageEvent])
  def onTiming(event: Received[TimingEvent])
}


