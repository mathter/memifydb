package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.space.{KeyValueOperations, Space, SpaceFactory}
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.{Assertions, BeforeEach, Test}

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
 * <p>
 * Key-Value operations.
 */
class SimpleSpaceTest {
  private val spaceName = RandomStringUtils.insecure().nextAlphabetic(10)

  private val valueSerelizationFactory = ValueSerelizationFactory("cbor.fasterxml")

  private val spaceFactory = SpaceFactory(Const.id)

  private var operations: KeyValueOperations = _

  @BeforeEach
  def init(): Unit = {
    this.operations = this.spaceFactory.instance(this.spaceName)
      .asInstanceOf[Space[KeyValueOperations]]
      .operations
  }

  @Test
  def test(): Unit = {
    val key0 = this.valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val value0 = this.valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))

    val key1 = this.valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val value1 = this.valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))

    val opt0 = this.operations.put(key0, value0)
    Assertions.assertNotNull(opt0)
    Assertions.assertTrue(opt0.isEmpty)

    val opt1 = this.operations.put(key0, value1)
    Assertions.assertNotNull(opt1)
    Assertions.assertTrue(opt1.notEmpty)
    Assertions.assertEquals(value0, opt1.get)

    val opt2 = this.operations(key1)
    Assertions.assertNotNull(opt2)
    Assertions.assertTrue(opt2.isEmpty)
  }

  @Test
  def testNullKey(): Unit = {
    Assertions.assertThrows(classOf[NullPointerException], () => this.operations.put(null, null))
  }
}
