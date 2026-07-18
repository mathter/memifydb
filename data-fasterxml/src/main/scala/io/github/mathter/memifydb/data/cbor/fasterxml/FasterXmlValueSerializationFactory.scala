package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data
import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.data.cbor.fasterxml.FasterXmlValueSerializationFactory.{valueDeserializer, valueSerializer}
import tools.jackson.databind.ObjectMapper

class FasterXmlValueSerializationFactory extends ValueSerelizationFactory {
  override def id: String = FasterXmlValueSerializationFactory.id

  override def serializer: data.ValueSerializer = valueSerializer

  override def deserializer: data.ValueDeserializer = valueDeserializer
}

object FasterXmlValueSerializationFactory {
  val id = "cbor.fasterxml"

  private given mapper: ObjectMapper = ObjectMapper()

  private val valueSerializer = new ValueSerializer

  private val valueDeserializer = new ValueDeserializer()
}