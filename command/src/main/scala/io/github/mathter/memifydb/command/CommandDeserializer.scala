package io.github.mathter.memifydb.command

import java.io.InputStream

trait CommandDeserializer {
  def deserialize[T <: Command](is: InputStream): T
}
