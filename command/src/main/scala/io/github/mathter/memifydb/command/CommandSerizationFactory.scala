package io.github.mathter.memifydb.command

trait CommandSerizationFactory {
  def serializer: CommandSerializer

  def deserializer: CommandDeserializer
}