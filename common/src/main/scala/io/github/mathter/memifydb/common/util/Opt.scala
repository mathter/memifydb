package io.github.mathter.memifydb.common.util

sealed abstract class Opt[+A] extends IterableOnce[A] with Product with Serializable {

  import Opt.{None, Some}

  def get: A

  override def iterator: Iterator[A] = {
    if (isEmpty) collection.Iterator.empty else collection.Iterator.single(this.get)
  }

  override def knownSize: Int = if (isEmpty) 0 else 1

  inline final def isEmpty: Boolean = this eq None

  inline final def notEmpty: Boolean = isDefined

  inline final def isDefined: Boolean = !isEmpty

  final def isPresent(): Boolean = !isEmpty

  infix inline final def contains[A1 >: A](elem: A1): Boolean = isDefined && this.get == elem

  inline final def exists(p: A => Boolean): Boolean = isDefined && p(this.get)

  inline final def forall(p: A => Boolean): Boolean = isEmpty || p(this.get)

  inline final def foreach[U](f: A => U): Unit = if (isDefined) f(this.get)

  infix inline final def getOrElse[B >: A](default: => B): B = if (isEmpty) default else this.get

  infix inline final def orElse[B >: A](alternative: => Opt[B]): Opt[B] = if (isEmpty) alternative else this

  inline final def orNull[A1 >: A](implicit ev: Null <:< A1): A1 = this getOrElse ev(null)

  infix inline final def map[B](f: A => B): Opt[B] = if (isEmpty) None else Some(f(this.get))

  inline final def fold[B](ifEmpty: => B)(f: A => B): B = if (isEmpty) ifEmpty else f(this.get)

  inline final def flatMap[B](f: A => Opt[B]): Opt[B] = if (isEmpty) None else f(this.get)

  inline def flatten[B](implicit ev: A <:< Opt[B]): Opt[B] = if (isEmpty) None else ev(this.get)

  infix inline final def filter(p: A => Boolean): Opt[A] = if (isEmpty || p(this.get)) this else None

  infix inline final def filterNot(p: A => Boolean): Opt[A] = if (isEmpty || !p(this.get)) this else None

  inline final def zip[A1 >: A, B](that: Opt[B]): Opt[(A1, B)] =
    if (isEmpty || that.isEmpty) None else Some((this.get, that.get))

  final def unzip[A1, A2](implicit asPair: A <:< (A1, A2)): (Opt[A1], Opt[A2]) = {
    if (isEmpty) {
      (None, None)
    } else {
      val e = asPair(this.get)
      (Some(e._1), Some(e._2))
    }
  }

  def toList: List[A] = if (isEmpty) List() else new::(this.get, Nil)
}

object Opt {
  inline def apply[A](x: A): Opt[A] = Some(x)

  inline def empty[A]: Opt[A] = None

  private final case class Some[+A](value: A) extends Opt[A] {
    infix def get: A = value
  }

  private case object None extends Opt[Nothing] {
    inline def get: Nothing = throw new NoSuchElementException("None.get")
  }
}