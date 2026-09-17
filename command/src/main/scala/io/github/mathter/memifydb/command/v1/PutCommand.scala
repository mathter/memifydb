package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandDesc, Prefix, Sequence}
import io.github.mathter.memifydb.common.data.Value

class PutCommand(sequence: Sequence,
                 val spaceName: Value[String],
                 val key: Value[?],
                 val value: Value[?])
  extends AbstractCommand(sequence) {
}

object PutCommand extends CommandDesc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x01))
}