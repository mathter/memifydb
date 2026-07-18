package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import tools.jackson.databind.ObjectMapper

import java.lang.ref.SoftReference
import java.util
import scala.reflect.ClassTag

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
