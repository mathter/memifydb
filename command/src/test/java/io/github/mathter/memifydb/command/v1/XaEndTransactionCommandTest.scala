package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandSerizationFactoryProvider, Sequence}
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
class XaEndTransactionCommandTest {
  val provider = CommandSerizationFactoryProvider.apply(Const.id)

  val factory = this.provider.instance().asInstanceOf[CommandSerizationFactoryV1]

  @Test
  def test(): Unit = {
    val xid = Xid(
      RandomUtils.insecure().randomInt(),
      RandomUtils.insecure().randomBytes(10),
      RandomUtils.insecure().randomBytes(10)
    )
    val flags = RandomUtils.insecure().randomInt()
    val cmd = new XaEndTransactionCommand(
      Sequence(RandomUtils.insecure().randomLong()),
      xid,
      flags
    )

    val deserializedCmd = Using(new ByteArrayOutputStream()) {
      os =>
        factory.serializer.serialize(os, cmd)
        os.toByteArray
    }.flatMap {
        a =>
          Using(new ByteArrayInputStream(a)) {
            is =>
              this.factory.deserializer.deserialize[XaEndTransactionCommand](is)
          }
      }
      .get
    Assertions.assertNotNull(deserializedCmd)
    Assertions.assertEquals(cmd.sequence, deserializedCmd.sequence)
    Assertions.assertEquals(cmd.xid, deserializedCmd.xid)
    Assertions.assertEquals(cmd.flags, deserializedCmd.flags)
  }
}
