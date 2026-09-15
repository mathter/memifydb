package io.github.mathter.memifydb.transaction.xa

import javax.transaction.xa.Xid as JavaxXid

case class Xid(val formatId: Int, val globalTransactionId: Array[Byte], val branchQualifier: Array[Byte]) extends JavaxXid {
  override def getFormatId: Int = this.formatId

  override def getGlobalTransactionId: Array[Byte] = this.globalTransactionId

  override def getBranchQualifier: Array[Byte] = this.branchQualifier

  override def toString: String = {
    s"Xid:${formatId}:0x${this.toHex(this.globalTransactionId)}:0x${this.toHex(this.branchQualifier)}"
  }

  private def toHex(array: Array[Byte]): String = {
    if (array != null && !array.isEmpty) {
      val sb = new StringBuilder
      array.foreach(e => sb.addAll("%02X" format e))

      sb.toString
    } else {
      ""
    }
  }
}
