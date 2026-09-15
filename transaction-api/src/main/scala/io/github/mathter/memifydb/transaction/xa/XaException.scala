package io.github.mathter.memifydb.transaction.xa

import javax.transaction.xa.XAException

class XaException private(message: String) extends XAException(message) {
  def this(message: String, errorCode: Int) = {
    this(message)
    this.errorCode = errorCode
  }
}