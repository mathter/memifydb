package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandSerizationFactoryProvider, Sequence}
import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}
import org.junit.jupiter.api.{Assertions, Test}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.util.Using

class GetCommandTest {
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
