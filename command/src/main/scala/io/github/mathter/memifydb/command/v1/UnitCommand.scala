package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Desc, Prefix, Sequence}

class UnitCommand(sequence: Sequence) extends AbstractCommand(sequence) {

}

object UnitCommand extends Desc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x04))
}