package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.{Value, ValueDeserializer as apiValueDeserializer}
import tools.jackson.databind.ObjectMapper

import scala.reflect.ClassTag
/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */
private class ValueDeserializer(using val mapper: ObjectMapper) extends apiValueDeserializer {
  override def deserialize[T](raw: Array[Byte])(using classTag: ClassTag[T]): Value[T] =
    new RawValue[T](raw)
}
