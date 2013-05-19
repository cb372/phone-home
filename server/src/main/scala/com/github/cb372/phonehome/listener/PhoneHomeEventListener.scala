package com.github.cb372.phonehome
package listener

import model._

trait PhoneHomeEventListener {
  def onError(event: Timestamped[ErrorEvent])
  def onMessage(event: Timestamped[MessageEvent])
}


