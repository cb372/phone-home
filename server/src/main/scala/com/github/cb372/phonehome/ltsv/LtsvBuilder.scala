package com.github.cb372.phonehome.ltsv

/**
 * Author: chris
 * Created: 5/25/13
 */
trait LtsvBuilder {

  def toLtsv[T: LtsvFormat](e: T): String = {
    val sb = new StringBuilder()

    // serialize to LTSV
    implicitly[LtsvFormat[T]].appendLtsv(e, sb)

    // Remove trailing tab, in order to produce valid LTSV
    if (sb.last == '\t')
      sb.deleteCharAt(sb.length - 1)

    sb.toString()
  }

}
