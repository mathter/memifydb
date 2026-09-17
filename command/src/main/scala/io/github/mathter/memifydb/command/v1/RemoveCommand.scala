package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandDesc, Prefix, Sequence}
import io.github.mathter.memifydb.common.data.Value

class RemoveCommand(sequence: Sequence,
                    val spaceName: Value[String],
                    val key: Value[?])
  extends AbstractCommand(sequence) {
}

object RemoveCommand extends CommandDesc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x03))
}