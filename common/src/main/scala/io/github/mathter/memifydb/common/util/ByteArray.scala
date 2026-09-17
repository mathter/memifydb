package io.github.mathter.memifydb.common.util

import java.io.{EOFException, IOException, InputStream, OutputStream}
import java.lang
import java.lang.invoke.{MethodHandles, VarHandle}
import java.nio.ByteOrder

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
object ByteArray {
  private val SHORT = create(classOf[Array[Short]])

  private val CHAR = create(classOf[Array[Char]])

  private val INT = create(classOf[Array[Int]])

  private val LONG = create(classOf[Array[Long]])

  private val FLOAT = create(classOf[Array[Float]])

  private val DOUBLE = create(classOf[Array[Double]])

  private def create(viewArrayClass: Class[_]) = MethodHandles.byteArrayViewVarHandle(viewArrayClass, ByteOrder.BIG_ENDIAN)

  inline def getByteRaw(buf: Array[Byte], offset: Int): Byte = buf(offset)

  inline def setByteRaw(buf: Array[Byte], offset: Int, value: Byte): Unit = {
    buf(offset) = value
  }

  inline def getShortRaw(buf: Array[Byte], offset: Int): Short = SHORT.get(buf, offset).asInstanceOf[Short]

  inline def setShortRaw(buf: Array[Byte], offset: Int, value: Short): Unit = {
    SHORT.set(buf, offset, value)
  }

  inline def getCharRaw(buf: Array[Byte], offset: Int): Char = CHAR.get(buf, offset).asInstanceOf[Char]

  inline def setCharRaw(buf: Array[Byte], offset: Int, value: Char): Unit = {
    CHAR.set(buf, offset, value)
  }

  inline def getIntRaw(buf: Array[Byte], offset: Int): Int = INT.get(buf, offset).asInstanceOf[Int]

  inline def setIntRaw(buf: Array[Byte], offset: Int, value: Int): Unit = {
    INT.set(buf, offset, value)
  }

  inline def getLongRaw(buf: Array[Byte], offset: Int): Long = LONG.get(buf, offset).asInstanceOf[Long]

  inline def setLongRaw(buf: Array[Byte], offset: Int, value: Long): Unit = {
    LONG.set(buf, offset, value)
  }

  inline def getFloat(buf: Array[Byte], offset: Int): Float = lang.Float.intBitsToFloat(INT.get(buf, offset).asInstanceOf[Int]).toFloat

  inline def setFloat(buf: Array[Byte], offset: Int, value: Float): Unit = {
    INT.set(buf, offset, lang.Float.floatToIntBits(value))
  }

  inline def getFloatRaw(buf: Array[Byte], offset: Int): Float = FLOAT.get(buf, offset).asInstanceOf[Float]

  inline def setFloatRaw(buf: Array[Byte], offset: Int, value: Float): Unit = {
    FLOAT.set(buf, offset, value)
  }

  inline def getDouble(buf: Array[Byte], offset: Int): Double = lang.Double.longBitsToDouble(LONG.get(buf, offset).asInstanceOf[Long])

  inline def setDouble(buf: Array[Byte], offset: Int, value: Double): Unit = {
    LONG.set(buf, offset, lang.Double.doubleToLongBits(value))
  }

  inline def getDoubleRaw(buf: Array[Byte], offset: Int): Double = DOUBLE.get(buf, offset).asInstanceOf[Double]

  inline def setDoubleRaw(buf: Array[Byte], offset: Int, value: Double): Unit = {
    DOUBLE.set(buf, offset, value)
  }

  @throws[IOException]
  inline def readByteRaw(is: InputStream): Byte = {
    val result = is.read
    if (result == -1) throw new EOFException("Unexpected end of stream")
    result.toByte
  }

  @throws[IOException]
  inline def writeByteRaw(os: OutputStream, value: Byte): Unit = {
    os.write(value.asInstanceOf[Int])
  }

  @throws[IOException]
  inline def readShortRaw(is: InputStream): Short = {
    val buf = new Array[Byte](lang.Short.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    SHORT.get(buf, 0).asInstanceOf[Short]
  }

  @throws[IOException]
  inline def writeShortRaw(os: OutputStream, value: Short): Unit = {
    val buf = new Array[Byte](lang.Short.BYTES)
    SHORT.set(buf, 0, value)
    os.write(buf)
  }

  @throws[IOException]
  inline def readCharRaw(is: InputStream): Char = {
    val buf = new Array[Byte](Character.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    CHAR.get(buf, 0).asInstanceOf[Char]
  }

  @throws[IOException]
  inline def writeCharRaw(os: OutputStream, value: Char): Unit = {
    val buf = new Array[Byte](Character.BYTES)
    CHAR.set(buf, 0, value)
    os.write(buf)
  }

  @throws[IOException]
  inline def readIntRaw(is: InputStream): Int = {
    val buf = new Array[Byte](Integer.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    INT.get(buf, 0).asInstanceOf[Int]
  }

  @throws[IOException]
  inline def writeIntRaw(os: OutputStream, value: Int): Unit = {
    val buf = new Array[Byte](Integer.BYTES)
    INT.set(buf, 0, value)
    os.write(buf)
  }

  @throws[IOException]
  inline def readLongRaw(is: InputStream): Long = {
    val buf = new Array[Byte](lang.Long.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    LONG.get(buf, 0).asInstanceOf[Long]
  }

  @throws[IOException]
  inline def writeLongRaw(os: OutputStream, value: Long): Unit = {
    val buf = new Array[Byte](lang.Long.BYTES)
    LONG.set(buf, 0, value)
    os.write(buf)
  }

  @throws[IOException]
  inline def readFloat(is: InputStream): Float = {
    val buf = new Array[Byte](Integer.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    lang.Float.intBitsToFloat(INT.get(buf, 0).asInstanceOf[Int])
  }

  @throws[IOException]
  inline def writeFloat(os: OutputStream, value: Float): Unit = {
    val buf = new Array[Byte](Integer.BYTES)
    INT.set(buf, 0, lang.Float.floatToRawIntBits(value))
    os.write(buf)
  }

  @throws[IOException]
  inline def readFloatRaw(is: InputStream): Float = {
    val buf = new Array[Byte](lang.Float.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    FLOAT.get(buf, 0).asInstanceOf[Float]
  }

  @throws[IOException]
  inline def writeFloatRaw(os: OutputStream, value: Float): Unit = {
    val buf = new Array[Byte](lang.Float.BYTES)
    FLOAT.set(buf, 0, value)
    os.write(buf)
  }

  @throws[IOException]
  inline def readDouble(is: InputStream): Double = {
    val buf = new Array[Byte](lang.Long.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    lang.Double.longBitsToDouble(LONG.get(buf, 0).asInstanceOf[Long])
  }

  @throws[IOException]
  inline def writeDouble(os: OutputStream, value: Double): Unit = {
    val buf = new Array[Byte](lang.Long.BYTES)
    LONG.set(buf, 0, lang.Double.doubleToLongBits(value))
    os.write(buf)
  }

  @throws[IOException]
  inline def readDoubleRaw(is: InputStream): Double = {
    val buf = new Array[Byte](lang.Double.BYTES)
    val read = is.readNBytes(buf, 0, buf.length)
    if (read != buf.length) throw new EOFException("Expected " + buf.length + " bytes, got " + read)
    DOUBLE.get(buf, 0).asInstanceOf[Double]
  }

  @throws[IOException]
  inline def writeDoubleRaw(os: OutputStream, value: Double): Unit = {
    val buf = new Array[Byte](lang.Double.BYTES)
    DOUBLE.set(buf, 0, value)
    os.write(buf)
  }

  infix def toHex(array: Array[Byte]): String = {
    if (array != null && !array.isEmpty) {
      val sb = new StringBuilder
      array.foreach(e => sb.addAll("%02X" format e))

      sb.toString
    } else {
      ""
    }
  }

  inline def hashCode(x: Array[Byte]): Int = {
    var result = 0

    if (x != null) {
      x.foreach(result + _)
    }

    result
  }
}
