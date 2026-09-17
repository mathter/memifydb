package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.util.IOUtil
import io.github.mathter.memifydb.command.{Command, CommandSerializer, Sequence}

import java.io.OutputStream

private class CommandSerializerV1 extends CommandSerializer {
  override def serialize(using os: OutputStream, command: Command): Boolean = {
    os.write(command.prefix.toByteArray)
    IOUtil.write(command.sequence)

    command match {
      case x: GetCommand => {
        IOUtil.write(x.spaceName)
        IOUtil.write(x.key)

        true
      }

      case x: PutCommand => {
        IOUtil.write(x.spaceName)
        IOUtil.write(x.key)
        IOUtil.write(x.value)

        true
      }

      case x: XaCommitTransactionCommand => {
        IOUtil.write(x.xid)
        IOUtil.write(x.onePhase)

        true
      }

      case _ => false
    }
  }
}
