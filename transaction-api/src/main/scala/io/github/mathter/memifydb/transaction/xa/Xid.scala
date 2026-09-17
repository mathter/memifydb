package io.github.mathter.memifydb.transaction.xa

import javax.transaction.xa.Xid as JavaxXid

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @param formatId
 * @param globalTransactionId
 * @param branchQualifier
 */
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

  override def hashCode(): Int = {
    this.formatId + Xid.hashCode(this.globalTransactionId) + Xid.hashCode(this.branchQualifier)
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case x: JavaxXid => this.formatId == x.getFormatId
        && Xid.equals(x.getGlobalTransactionId, this.branchQualifier)
        && Xid.equals(x.getBranchQualifier, this.branchQualifier)
      case _ => false
    }
  }
}

object Xid {
  inline def hashCode(x: Array[Byte]): Int = {
    var result = 0

    if (x != null) {
      x.foreach(result + _)
    }

    result
  }

  inline def equals(left: Array[Byte], right: Array[Byte]): Boolean = {
    if (left eq right) {
      true
    } else {
      if (left != null) {
        if (right != null) {
          if (left.length == right.length) {
            for (i <- left.indices) {
              left(i) == right(i)
            }

            true
          } else {
            false
          }
        } else {
          false
        }
      } else {
        if (right != null) {
          false
        } else {
          true
        }
      }
    }
  }
}
