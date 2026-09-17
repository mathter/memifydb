package io.github.mathter.memifydb.command

import java.io.{IOException, OutputStream}


trait CommandSerializer {
  @throws[IOException]
  def serialize(os: OutputStream, command: Command): Boolean
}
