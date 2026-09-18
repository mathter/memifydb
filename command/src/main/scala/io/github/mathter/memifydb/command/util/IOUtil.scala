package io.github.mathter.memifydb.command.util

import io.github.mathter.memifydb.command.Sequence
import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.ByteArray
import io.github.mathter.memifydb.transaction.xa

import java.io.{InputStream, OutputStream}
import javax.transaction.xa.Xid

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
object IOUtil {
  inline def write(x: Boolean)(using os: OutputStream): OutputStream = {
    os.write(if x then 1 else 0)
    os
  }

  inline def readBoolean(using is: InputStream): Boolean = {
    is.readNBytes(1)(0) > 0
  }

  inline def write(array: Array[Byte])(using os: OutputStream): OutputStream = {
    ByteArray.writeIntRaw(os, if array != null then array.length else -1)
    os.write(array)
    os
  }

  inline def readArray(using is: InputStream): Array[Byte] = {
    val length = ByteArray.readIntRaw(is)

    is.readNBytes(length)
  }

  inline def write(sequence: Sequence)(using os: OutputStream): OutputStream = {
    ByteArray.writeLongRaw(os, sequence.value)
    os
  }

  inline def readSequence(using is: InputStream): Sequence = {
    new Sequence(ByteArray.readLongRaw(is))
  }

  inline def write(xid: Xid)(using os: OutputStream): OutputStream = {
    ByteArray.writeIntRaw(os, xid.getFormatId)
    this.write(xid.getGlobalTransactionId)
    this.write(xid.getBranchQualifier)

    os
  }

  inline def readXid(using is: InputStream): Xid = {
    val formatId = ByteArray.readIntRaw(is)
    val globalTransactionId = this.readArray
    val branchQualifier = this.readArray

    xa.Xid(formatId, globalTransactionId, branchQualifier)
  }

  inline def write[T](value: Value[T])(using os: OutputStream): OutputStream = {
    val raw = value.raw
    ByteArray.writeIntRaw(os, raw.length)
    os.write(raw)
    os
  }

  inline def readValueRaw[T](using is: InputStream): Array[Byte] = {
    val lenght = ByteArray.readIntRaw(is)
    is.readNBytes(lenght)
  }
}
