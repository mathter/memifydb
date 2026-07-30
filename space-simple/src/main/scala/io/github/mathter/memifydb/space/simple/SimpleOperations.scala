package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import io.github.mathter.memifydb.space.KeyValueOperations

import java.util.concurrent.locks.{Lock, ReadWriteLock, ReentrantReadWriteLock}
import scala.collection.mutable

private class SimpleOperations extends KeyValueOperations {
  private val readWriteLock: ReadWriteLock = new ReentrantReadWriteLock()

  private val readLock: Lock = this.readWriteLock.readLock()

  private val writeLock: Lock = this.readWriteLock.writeLock()

  private val map: mutable.Map[Value[Any], Opt[Value[? <: Any]]] = new mutable.HashMap()

  /**
   * Method returns the value associated with the specified key.
   *
   * @param key key can't be null.
   * @return Opt wrapper of value.
   */
  override def apply[V](key: Value[Any]): Opt[Value[V]] = {
    this.readLock.lock()
    try {
      this.map.getOrElse(key, Opt.empty).asInstanceOf[Opt[Value[V]]]
    } finally {
      this.readLock.unlock()
    }
  }

  /**
   * Method puts value by key. Key can't be null.
   *
   * @param key   key can't be null.
   * @param value value can be null.
   * @return previous value specified by the key or [[Opt.empty]] otherwise.
   * @throws DifferentKeyTypeException if the key is of a different type from the keys in the storage.
   */
  override def update[V, R](key: Value[Any], value: Value[V]): Opt[Value[R]] = {
    this.writeLock.lock()

    try {
      val prev = this.map.put(key, Opt(value))
      if (prev != null) prev.asInstanceOf[Opt[Value[R]]] else Opt.empty
    } finally {
      this.writeLock.unlock()
    }
  }

  /**
   * Method removes value from space by the key.
   *
   * @param key key can't be null.
   * @return previous value specified by the key or [[Opt.empty]] otherwise.
   */
  override def remove[R](key: Value[Any]): Opt[Value[R]] = {
    this.writeLock.lock()

    try {
      val prev = this.map.remove(key)
      if (prev != null) prev.asInstanceOf[Opt[Value[R]]] else Opt.empty
    } finally {
      this.writeLock.unlock()
    }
  }

  override def clear(): Unit = this.map.clear()
}
