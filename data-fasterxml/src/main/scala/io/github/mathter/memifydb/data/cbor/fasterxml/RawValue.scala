package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import tools.jackson.databind.ObjectMapper

import java.lang.ref.{Reference, SoftReference}
import java.util
import scala.reflect.ClassTag

class RawValue[T](private val x: Array[Byte])
                 (using private val classTag: ClassTag[T], private val mapper: ObjectMapper)
  extends Value[T] {
  private var reference: Reference[Opt[T]] = null

  inline override def raw: Array[Byte] = x

  inline override def get(using classTag: ClassTag[T]): T = {
    var now: T = null.asInstanceOf[T]

    if (this.reference == null && (now = this.reference.get()) == null) {
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
