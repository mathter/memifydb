package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Prefix, PrefixFactory}
import io.github.mathter.memifydb.common.util.ByteArray

private class PrefixV1 private(val x: Array[Byte]) extends Prefix {
  override def toByteArray: Array[Byte] = this.x

  override def toString: String = {
    "PrefixV1(0x" + ByteArray.toHex(this.x) + ")"
  }

  override def hashCode(): Int = {
    this.x(1).asInstanceOf[Int] << 8 & this.x(0)
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case x: PrefixV1 => x.x(0) == this.x(0) && x.x(1) == this.x(1)
      case _ => false
    }
  }
}

object PrefixV1 extends PrefixFactory[PrefixV1] {
  val BYTES = 2

  override def apply(x: Array[Byte]): PrefixV1 = {
    if (x != null && x.length == BYTES) {
      new PrefixV1(x)
    } else {
      throw new IllegalArgumentException(s"${x} is not valid prefix v1! Required not empty byte array length of 2.")
    }
  }

  override def unapply(x: PrefixV1): Array[Byte] = x.x
}
