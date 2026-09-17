package io.github.mathter.memifydb.command.v1

object Const {
  final val id = classOf[CommandSerizationFactoryProviderV1].getName

  final val propertyValueSerializationFactory = "serialization-factory-id"

  final val defaultValueSerializationFactoryId = "cbor.fasterxml"
}
