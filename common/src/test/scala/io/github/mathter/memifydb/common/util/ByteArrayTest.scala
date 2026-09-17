package io.github.mathter.memifydb.common.util

import org.apache.commons.lang3.RandomUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{Arguments, ArgumentsProvider, ArgumentsSource}
import org.junit.jupiter.params.support.ParameterDeclarations

import java.io.{ByteArrayInputStream, IOException, InputStream}
import java.lang
import java.util.stream.Stream

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
 */
class ByteArrayTest {
  @ParameterizedTest
  @ArgumentsSource(classOf[ByteArrayTest.type])
  @throws[IOException]
  def testTest[T](originList: List[T],
                  elementSize: Int,
                  set: (a: Array[Byte], offset: Int, e: T) => Unit,
                  get: (a: Array[Byte], offset: Int) => T,
                  read: (is: InputStream) => T): Unit = {
    val origin: Array[Any] = originList.toArray
    val result: Array[Byte] = new Array[Byte](elementSize * origin.length)
    for (i <- origin.indices) {
      set(result, i * elementSize, origin(i).asInstanceOf[T])
    }
    for (i <- origin.indices) {
      Assertions.assertEquals(origin(i), get(result, elementSize * i))
    }
    val is: InputStream = new ByteArrayInputStream(result)
    for (i <- origin.indices) {
      Assertions.assertEquals(origin(i), read(is))
    }
  }
}

object ByteArrayTest extends ArgumentsProvider {
  private val count = 1000

  override def provideArguments(parameters: ParameterDeclarations, context: ExtensionContext): Stream[? <: Arguments] = {
    Stream.of(
      Arguments.of(
        build(count, RandomUtils.insecure().randomInt().toByte),
        lang.Byte.BYTES,
        ByteArray.setByteRaw,
        ByteArray.getByteRaw,
        ByteArray.readByteRaw
      ),
      Arguments.of(
        build(count, RandomUtils.insecure().randomInt().toChar),
        Character.BYTES,
        ByteArray.setCharRaw,
        ByteArray.getCharRaw,
        ByteArray.readCharRaw
      ),
      Arguments.of(
        build(count, RandomUtils.insecure().randomInt()),
        Integer.BYTES,
        ByteArray.setIntRaw,
        ByteArray.getIntRaw,
        ByteArray.readIntRaw
      ),
      Arguments.of(
        build(count, RandomUtils.insecure().randomLong()),
        lang.Long.BYTES,
        ByteArray.setLongRaw,
        ByteArray.getLongRaw,
        ByteArray.readLongRaw
      ),
      Arguments.of(
        build(count, RandomUtils.insecure().randomFloat()),
        lang.Float.BYTES,
        ByteArray.setFloatRaw,
        ByteArray.getFloatRaw,
        ByteArray.readFloatRaw
      ),
      Arguments.of(
        build(count, RandomUtils.insecure().randomFloat()),
        lang.Float.BYTES,
        ByteArray.setFloat,
        ByteArray.getFloat,
        ByteArray.readFloat
      ),
      Arguments.of(build(count, RandomUtils.insecure().randomDouble()),
        lang.Double.BYTES,
        ByteArray.setDoubleRaw,
        ByteArray.getDoubleRaw,
        ByteArray.readDoubleRaw
      ),
      Arguments.of(
        build(count, RandomUtils.insecure().randomDouble()),
        lang.Double.BYTES,
        ByteArray.setDouble,
        ByteArray.getDouble,
        ByteArray.readDouble
      )
    )
  }

  def build[T](cnt: Int, supplier: => T): List[T] = {
    (0 to cnt).map(e => supplier).toList
  }
}
