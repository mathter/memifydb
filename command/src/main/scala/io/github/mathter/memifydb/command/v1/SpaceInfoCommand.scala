package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Desc, Prefix, Sequence}

class SpaceInfoCommand(sequence: Sequence, val serializationProtocol: String) extends AbstractCommand(sequence) {
}

object SpaceInfoCommand extends Desc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x07))
}
