package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.space.{KeyValueOperations, Space, SpaceFactory}
import io.github.mathter.memifydb.transaction.xa.{XaException, Xid}
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.{Assertions, Test}

import java.nio.charset.StandardCharsets
import javax.transaction.xa.XAResource

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
class XaSimpleSpaceTest {
  private val spaceName = RandomStringUtils.insecure().nextAlphabetic(10)

  private val valueSerelizationFactory = ValueSerelizationFactory("cbor.fasterxml")

  private val space = SpaceFactory(Const.id).instance(spaceName, Map(Const.propertyTransactionTimeout -> Int.MaxValue)).asInstanceOf[Space[KeyValueOperations]]

  @Test
  def testUseOperationsNoValidResource(): Unit = {
    val xid = Xid(
      0,
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8),
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8)
    )

    val xaResourceProvider = this.space.xaResource
    Assertions.assertThrowsExactly(classOf[XaException], () => xaResourceProvider.xa(xid))
  }

  @Test
  def testCommitTwoPhase(): Unit = {
    val xid = Xid(
      0,
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8),
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8)
    )
    val key = valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val value = valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val xaResource = this.space.xaResource

    xaResource.start(xid, XAResource.TMNOFLAGS)
    val ops = xaResource.xa(xid)

    ops.put(key, value)
    Assertions.assertTrue(this.space.operations.get(key).isEmpty)
    xaResource.end(xid, XAResource.TMSUCCESS)
    xaResource.prepare(xid)
    xaResource.commit(xid, false)

    val o = this.space.operations.get(key)
    Assertions.assertNotNull(o)
    Assertions.assertTrue(o.notEmpty)
    Assertions.assertEquals(value, o.get)
  }

  @Test
  def testCommitOnePhase(): Unit = {
    val xid = Xid(
      0,
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8),
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8)
    )
    val key = valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val value = valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val xaResource = this.space.xaResource

    xaResource.start(xid, XAResource.TMNOFLAGS)
    val ops = xaResource.xa(xid)

    ops.put(key, value)
    Assertions.assertTrue(this.space.operations.get(key).isEmpty)
    xaResource.end(xid, XAResource.TMSUCCESS)
    xaResource.commit(xid, true)

    val o = this.space.operations.get(key)
    Assertions.assertNotNull(o)
    Assertions.assertTrue(o.notEmpty)
    Assertions.assertEquals(value, o.get)
  }

  @Test
  def testRollback(): Unit = {
    val xid = Xid(
      0,
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8),
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8)
    )
    val key = valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val value = valueSerelizationFactory.serializer.serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val xaResource = this.space.xaResource

    xaResource.start(xid, XAResource.TMNOFLAGS)
    val ops = xaResource.xa(xid)

    ops.put(key, value)
    Assertions.assertTrue(this.space.operations.get(key).isEmpty)
    xaResource.end(xid, XAResource.TMSUCCESS)
    xaResource.prepare(xid)
    xaResource.rollback(xid)

    val o = this.space.operations.get(key)
    Assertions.assertNotNull(o)
    Assertions.assertTrue(o.isEmpty)
  }

  @Test
  def testRecover(): Unit = {
    val xid = Xid(
      0,
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8),
      RandomStringUtils.insecure().nextAlphabetic(8).getBytes(StandardCharsets.UTF_8)
    )
    val xaResource = this.space.xaResource

    xaResource.start(xid, XAResource.TMNOFLAGS)
    xaResource.prepare(xid)
    var xids = xaResource.recover(0)

    Assertions.assertNotNull(xids)
    Assertions.assertEquals(0, xids.length)

    xids = xaResource.recover(XAResource.TMSTARTRSCAN)

    Assertions.assertNotNull(xids)
    Assertions.assertEquals(1, xids.length)
    Assertions.assertEquals(xid, xids(0))

    xaResource.rollback(xid)
  }
}
