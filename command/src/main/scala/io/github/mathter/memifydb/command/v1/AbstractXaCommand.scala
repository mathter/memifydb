package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.Sequence

import javax.transaction.xa.Xid

class AbstractXaCommand(sequence: Sequence, val xid: Xid) extends AbstractCommand(sequence) {
}
