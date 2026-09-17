package io.github.mathter.memifydb.command

class Sequence(val value: Long) extends AnyVal {
}

object Sequence {
  inline def apply(x: Long): Sequence = new Sequence(x)

  inline def unapply(x: Sequence): Long = x.value
}
