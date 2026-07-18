package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.{Value, ValueSerializer as apiValueSerializer}
import tools.jackson.databind.ObjectMapper

import scala.reflect.ClassTag

private class ValueSerializer(using val mapper: ObjectMapper) extends apiValueSerializer {
  override def serialize[T](v: T)(using classTag: ClassTag[T]): Value[T] = {
    v match {
      case x: RawValue[T] => x
      case x: Any => new JavaValue[T](x)
    }
  }
}
