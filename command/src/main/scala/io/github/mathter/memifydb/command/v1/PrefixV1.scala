package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Prefix, PrefixFactory}
import io.github.mathter.memifydb.common.util.ByteArray

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
 */
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
