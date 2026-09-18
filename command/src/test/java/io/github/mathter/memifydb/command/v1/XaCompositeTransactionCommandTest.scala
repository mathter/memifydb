package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{SerizationFactoryProvider, Sequence, v1}
import io.github.mathter.memifydb.transaction.xa.Xid
import org.apache.commons.lang3.RandomUtils
import org.junit.jupiter.api.{Assertions, Test}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.util.Using

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
class XaCompositeTransactionCommandTest {
  val provider: SerizationFactoryProvider = SerizationFactoryProvider.apply(Const.id)

  val factory: CommandSerizationFactoryV1 = this.provider.instance().asInstanceOf[CommandSerizationFactoryV1]

  @Test
  def test(): Unit = {
    val xids = (0 to RandomUtils.insecure().randomInt(10, 100)).map {
      e =>
        Xid(
          RandomUtils.insecure().randomInt(),
          RandomUtils.insecure().randomBytes(10),
          RandomUtils.insecure().randomBytes(10)
        )
    }.toList
    val cmd = new XaContainerCommand(
      Sequence(RandomUtils.insecure().randomLong()),
      xids.head,
      xids.drop(1)
        .map {
          xid =>
            new XaStartTransactionCommand(
              Sequence(RandomUtils.insecure().randomLong()),
              xid,
              0
            )
        } *
    )

    val deserializedCmd = Using(new ByteArrayOutputStream()) {
      os =>
        factory.serializer.serialize(os, cmd)
        os.toByteArray
    }.flatMap {
        a =>
          Using(new ByteArrayInputStream(a)) {
            is =>
              this.factory.deserializer.deserialize[XaContainerCommand](is)
          }
      }
      .get
    Assertions.assertNotNull(deserializedCmd)
    Assertions.assertEquals(cmd.sequence, deserializedCmd.sequence)
    Assertions.assertEquals(cmd.xid, deserializedCmd.xid)
    Assertions.assertEquals(cmd.commands.size, deserializedCmd.commands.size)

    cmd.commands.indices.foreach(e =>
      Assertions.assertEquals(cmd.commands(e).prefix, deserializedCmd.commands(e).prefix)
      Assertions.assertEquals(cmd.commands(e).sequence, deserializedCmd.commands(e).sequence)
      Assertions.assertEquals(
        cmd.commands(e).asInstanceOf[v1.XaStartTransactionCommand].xid,
        deserializedCmd.commands(e).asInstanceOf[v1.XaStartTransactionCommand].xid
      )
      Assertions.assertEquals(
        cmd.commands(e).asInstanceOf[v1.XaStartTransactionCommand].flags,
        deserializedCmd.commands(e).asInstanceOf[v1.XaStartTransactionCommand].flags
      )
    )
  }
}
