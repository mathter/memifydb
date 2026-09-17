package io.github.mathter.memifydb.command

trait Prefix {
  def toByteArray: Array[Byte]
}

trait PrefixFactory[T <: Prefix] {
  def apply(x: Array[Byte]): T

  def unapply(x: T): Array[Byte]
}