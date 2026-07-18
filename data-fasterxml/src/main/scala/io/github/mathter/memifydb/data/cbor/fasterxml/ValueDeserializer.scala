package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.{Value, ValueDeserializer as apiValueDeserializer}
import tools.jackson.databind.ObjectMapper

import scala.reflect.ClassTag

private class ValueDeserializer(using val mapper: ObjectMapper) extends apiValueDeserializer {
  override def deserialize[T](raw: Array[Byte])(using classTag: ClassTag[T]): Value[T] =
    new RawValue[T](raw)
}
