package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandDesc, Prefix, Sequence}

import javax.transaction.xa.Xid

class XaCommitTransactionCommand(sequence: Sequence, xid: Xid, val onePhase: Boolean) extends AbstractXaCommand(sequence, xid) {
}

object XaCommitTransactionCommand extends CommandDesc {
  val prefix: Prefix = PrefixV1(Array(0x10, 0x01))
}