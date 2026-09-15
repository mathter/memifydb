package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import io.github.mathter.memifydb.space.KeyValueOperations
import io.github.mathter.memifydb.space.simple.Status.NEW
import io.github.mathter.memifydb.space.simple.XaOperations.log

import java.util.logging.Logger
import javax.transaction.xa.Xid
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
 * <p>
 * Key-Value operations.
 */
private class XaOperations(val xid: Xid, val resource: SimpleXaResource) extends KeyValueOperations {
  val created = System.currentTimeMillis()

  val map: mutable.Map[Value[? <: Any], XaOperations.Record] = new mutable.HashMap()

  var status: Status = NEW

  /**
   * Method returns the value associated with the specified key.
   *
   * @param key key can't be null.
   * @return Opt wrapper of value.
   */
  override def apply[K, V](key: Value[K]): Opt[Value[V]] = {
    log.severe(() => s"apply(${key}) is called for resource=${this.resource}")

    this.map.getOrElse(key, Opt.empty).asInstanceOf[Opt[Value[V]]]
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
    log.severe(() => s"update(${key}, ${value}) is called for resource=${this.resource}")

    if (Status.STARTED eq this.status) {
      val origin = this.resource.space.operations.getSafe(key)

      this.map.put(key, XaOperations.Record(origin, Opt(value)))
        .getOrElse(Opt.empty)
        .asInstanceOf[Opt[Value[R]]]
    } else {
      throw new IllegalStateException(s"Can't put key ${key} with value ${value}, current state=${status} must be ${Status.STARTED}. XAResource#start must be call first. resource=${this.resource}")
    }
  }

  /**
   * Method removes value from space by the key.
   *
   * @param key key can't be null.
   * @return previous value specified by the key or [[Opt.empty]] otherwise.
   */
  override def remove[R](key: Value[Any]): Opt[Value[R]] = {
    log.severe(() => s"remove(${key}) is called for resource=${this.resource}")
    val origin = this.resource.space.operations.getSafe(key)

    this.map.put(key, XaOperations.Record(origin, null))
      .getOrElse(Opt.empty)
      .asInstanceOf[Opt[Value[R]]]
  }

  override def clear(): Unit = {
    log.severe(() => s"clear() is called for resource=${this.resource}")
    this.map.clear()
  }
}

private object XaOperations {
  private val log = Logger.getLogger(classOf[XaOperations].getName)

  case class Record(origin: Opt[Value[? <: Any]], thisVal: Opt[Value[? <: Any]]) {
  }
}
