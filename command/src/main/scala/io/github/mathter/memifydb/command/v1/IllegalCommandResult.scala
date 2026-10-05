package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Desc, Prefix, Sequence}

class IllegalCommandResult(sequence: Sequence, val data: Array[Byte]) extends AbstractResult(sequence) {
}

object IllegalCommandResult extends Desc {
  val prefix: Prefix = PrefixV1(Array(0xA0.asInstanceOf[Byte], 0x02))
}
