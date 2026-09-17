package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandSerizationFactoryProvider, Sequence}
import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.transaction.xa.Xid
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}
import org.junit.jupiter.api.{Assertions, Test}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.util.Using

class XaCommitTransactionCommandTest {
  val provider = CommandSerizationFactoryProvider.apply(Const.id)

  val factory = this.provider.instance().asInstanceOf[CommandSerizationFactoryV1]

  @Test
  def test(): Unit = {
    val xid = Xid(
      RandomUtils.insecure().randomInt(),
      RandomUtils.insecure().randomBytes(10),
      RandomUtils.insecure().randomBytes(10)
    )
    val onePhase = RandomUtils.insecure().randomBoolean()
    val cmd = new XaCommitTransactionCommand(
      Sequence(RandomUtils.insecure().randomLong()),
      xid,
      onePhase
    )

    val deserializedCmd = Using(new ByteArrayOutputStream()) {
      os =>
        factory.serializer.serialize(os, cmd)
        os.toByteArray
    }.flatMap {
        a =>
          Using(new ByteArrayInputStream(a)) {
            is =>
              this.factory.deserializer.deserialize[XaCommitTransactionCommand](is)
          }
      }
      .get
    Assertions.assertNotNull(deserializedCmd)
    Assertions.assertEquals(cmd.sequence, deserializedCmd.sequence)
    Assertions.assertEquals(cmd.xid, deserializedCmd.xid)
    Assertions.assertEquals(cmd.onePhase, deserializedCmd.onePhase)
  }
}
