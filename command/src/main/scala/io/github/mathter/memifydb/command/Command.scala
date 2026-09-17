package io.github.mathter.memifydb.command

trait Command extends CommandDesc {
  def sequence: Sequence
}