package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import io.github.mathter.memifydb.space.KeyValueOperations

import java.util.concurrent.locks.{Lock, ReadWriteLock, ReentrantReadWriteLock}
import scala.collection.mutable

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
 */
private class SimpleOperations extends KeyValueOperations {
  private val readWriteLock: ReadWriteLock = new ReentrantReadWriteLock()

  private val readLock: Lock = this.readWriteLock.readLock()

  private val writeLock: Lock = this.readWriteLock.writeLock()

  private val map: mutable.Map[Value[? <: Any], Opt[Value[? <: Any]]] = new mutable.HashMap()

  /**
   * Method returns the value associated with the specified key.
   *
   * @param key key can't be null.
   * @return Opt wrapper of value.
   */
  override def apply[K, V](key: Value[K]): Opt[Value[V]] = {
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
  override def update[K, V, R](key: Value[K], value: Value[V]): Opt[Value[R]] = {
    if (key != null) {
      this.writeLock.lock()

      try {
        this.map.put(key, Opt(value))
          .getOrElse(Opt.empty)
          .asInstanceOf[Opt[Value[R]]]
      } finally {
        this.writeLock.unlock()
      }
    } else {
      throw new NullPointerException("key parameter can't be null!")
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

  def withMapWrite[T](fun: mutable.Map[Value[? <: Any], Opt[Value[? <: Any]]] => T): T = {
    this.writeLock.lock()

    try {
      fun(this.map)
    }
    finally {
      this.writeLock.unlock()
    }
  }

  def withMapRead[T](fun: mutable.Map[Value[? <: Any], Opt[Value[? <: Any]]] => T): T = {
    this.readLock.lock()

    try {
      fun(this.map)
    }
    finally {
      this.readLock.unlock()
    }
  }

  def getSafe(key: Value[? <: Any]): Opt[Value[? <: Any]] = {
    this.readLock.lock()

    try {
      this.map.getOrElse(key, {
        this.readLock.unlock()
        this.writeLock.lock()

        try {
          this.map.getOrElse(key, null)
        } finally {
          this.readLock.lock()
          this.writeLock.unlock()
        }
      })
    } finally {
      this.readLock.unlock()
    }
  }
}
