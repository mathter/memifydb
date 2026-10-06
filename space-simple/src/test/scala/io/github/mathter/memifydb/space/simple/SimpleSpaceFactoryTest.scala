package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.space.{KeyValueOperations, Space, SpaceFactory}
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}
import org.junit.jupiter.api.{Assertions, Test}

import java.util.UUID

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
 */
class SimpleSpaceFactoryTest {
  @Test
  def testCreate(): Unit = {
    val spaceFactory = SpaceFactory.apply(Const.id)
    Assertions.assertNotNull(spaceFactory)

    val name = RandomStringUtils.insecure().nextAlphabetic(10)
    val space: SimpleSpace = spaceFactory.instance(name)
    Assertions.assertNotNull(space)
    Assertions.assertEquals(name, space.name)
    Assertions.assertNotNull(space.id)
    Assertions.assertEquals(Const.defaultTimeOut, space.transactionTimeout)
  }

  @Test
  def testCreateWithId(): Unit = {
    val spaceFactory = SpaceFactory.apply(Const.id)
    Assertions.assertNotNull(spaceFactory)

    val name = RandomStringUtils.insecure().nextAlphabetic(10)
    val id = UUID.randomUUID()
    val space: SimpleSpace = spaceFactory.instance(name, Map((Const.propertyId, id)))
    Assertions.assertNotNull(space)
    Assertions.assertEquals(name, space.name)
    Assertions.assertEquals(id, space.id)
    Assertions.assertEquals(Const.defaultTimeOut, space.transactionTimeout)
  }

  @Test
  def testCreateWithIdAndTransactionTimeOut(): Unit = {
    val spaceFactory = SpaceFactory.apply(Const.id)
    Assertions.assertNotNull(spaceFactory)

    val name = RandomStringUtils.insecure().nextAlphabetic(10)
    val transactionTimeout = RandomUtils.insecure().randomInt()
    val id = UUID.randomUUID()
    val space: SimpleSpace =
      spaceFactory.instance(name, Map(
        (Const.propertyId, id),
        (Const.propertyTransactionTimeout, transactionTimeout))
      )
    Assertions.assertNotNull(space)
    Assertions.assertEquals(name, space.name)
    Assertions.assertEquals(id, space.id)
    Assertions.assertEquals(transactionTimeout, space.transactionTimeout)
  }
}
