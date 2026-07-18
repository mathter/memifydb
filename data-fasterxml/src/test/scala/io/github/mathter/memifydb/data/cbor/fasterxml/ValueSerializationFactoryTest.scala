package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.{Value, ValueSerelizationFactory}
import org.junit.jupiter.api.{Assertions, Test}

class ValueSerializationFactoryTest {
  @Test
  def test = {
    val serializationFactory = ValueSerelizationFactory(FasterXmlValueSerializationFactory.id)
    val serializer = serializationFactory.serializer
    val deserializer = serializationFactory.deserializer

    val v = serializer.serialize("This is same string")
    Assertions.assertNotNull(v)
    Assertions.assertNotNull(v.raw)

    val r: Value[String] = deserializer.deserialize(v.raw)
    Assertions.assertNotNull(r)
    Assertions.assertEquals(r, v)
    Assertions.assertEquals(v, r)
  }
}
