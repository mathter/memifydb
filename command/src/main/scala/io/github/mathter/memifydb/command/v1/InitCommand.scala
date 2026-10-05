package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Desc, Prefix, Sequence}

class InitCommand(sequence: Sequence, val baseSerializationProtocal: String) extends AbstractCommand(sequence) {
}

object InitCommand extends Desc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x05))
}