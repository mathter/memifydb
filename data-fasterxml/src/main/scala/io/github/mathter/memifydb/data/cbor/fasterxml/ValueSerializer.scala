package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.{Value, ValueSerializer as apiValueSerializer}
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
private class ValueSerializer(using val mapper: ObjectMapper) extends apiValueSerializer {
  override def serialize[T](v: T)(using classTag: ClassTag[T]): Value[T] = {
    v match {
      case x: RawValue[T] => x
      case x: Any => new JavaValue[T](x)
    }
  }
}
