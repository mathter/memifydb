package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Desc, Prefix, Sequence}

class SayHelloCommand(sequence: Sequence, val login: String, val password: Array[Byte]) extends AbstractCommand(sequence) {

}

object SayHelloCommand extends Desc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x06))
}