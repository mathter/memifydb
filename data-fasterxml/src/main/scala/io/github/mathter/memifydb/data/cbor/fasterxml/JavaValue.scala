package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import tools.jackson.databind.ObjectMapper

import java.lang.ref.SoftReference
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
case class JavaValue[T](private val x: T)
                       (using val classTag: ClassTag[T], val mapper: ObjectMapper)
  extends Value[T] {
  private var reference: SoftReference[Opt[Array[Byte]]] = null

  override def raw: Array[Byte] = {
    var now: Array[Byte] = null

    if (this.reference == null || (now = this.reference.get()) == null) {
      now = this.calc
      this.reference = new SoftReference[Opt[Array[Byte]]](Opt(now))
      now
    } else {
      this.reference.get().get
    }
  }

  override def get(using classTag: ClassTag[T]): T = this.x

  inline def calc: Array[Byte] = this.mapper.writeValueAsBytes(this.x)

  override def hashCode(): Int = util.Arrays.hashCode(this.raw)

  override def equals(obj: Any): Boolean = super.equals(obj)
}
