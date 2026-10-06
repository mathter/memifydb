package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import tools.jackson.databind.ObjectMapper

import java.lang.ref.{Reference, SoftReference}
import java.util
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
class RawValue[T](private val x: Array[Byte])
                 (using private val classTag: ClassTag[T], private val mapper: ObjectMapper)
  extends Value[T] {
  private var reference: Reference[Opt[T]] = null

  inline override def raw: Array[Byte] = x

  inline override def get(using classTag: ClassTag[T]): T = {
    var now: T = null.asInstanceOf[T]

    if (this.reference == null || (now = this.reference.get()) == null) {
      now = this.calc
      this.reference = new SoftReference[Opt[T]](Opt(now))
      now
    } else {
      this.reference.get().get
    }
  }

  private inline def calc: T =
    this.mapper.readValue(x, 0, x.length, classTag.runtimeClass.asInstanceOf[Class[T]])

  override def toString: String = super.toString
}
