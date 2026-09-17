package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandSerizationFactoryProvider, Sequence}
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}
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
class RemoveCommandTest {
  val provider = CommandSerizationFactoryProvider.apply(Const.id)

  val factory = this.provider.instance().asInstanceOf[CommandSerizationFactoryV1]

  @Test
  def test(): Unit = {
    val spaceName = this.factory.valueSerelizationFactory.serializer
      .serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val key = this.factory.valueSerelizationFactory.serializer
      .serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val value = this.factory.valueSerelizationFactory.serializer
      .serialize(RandomStringUtils.insecure().nextAlphabetic(10))
    val cmd = new GetCommand(
      Sequence(RandomUtils.insecure().randomLong()),
      spaceName,
      key
    )

    val deserializedCmd = Using(new ByteArrayOutputStream()) {
      os =>
        factory.serializer.serialize(os, cmd)
        os.toByteArray
    }.flatMap {
        a =>
          Using(new ByteArrayInputStream(a)) {
            is =>
              this.factory.deserializer.deserialize[GetCommand](is)
          }
      }
      .get
    Assertions.assertNotNull(deserializedCmd)
    Assertions.assertEquals(cmd.sequence, deserializedCmd.sequence)
    Assertions.assertEquals(cmd.spaceName, deserializedCmd.spaceName)
    Assertions.assertEquals(cmd.key, deserializedCmd.key)
  }
}
