package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.common.data.Value
import io.github.mathter.memifydb.common.util.Opt
import io.github.mathter.memifydb.space.KeyValueOperations

private class SimpleOperations extends KeyValueOperations {
  /**
   * Method returns the value associated with the specified key.
   *
   * @param key key can't be null.
   * @return Opt wrapper of value.
   */
  override def apply[V](key: Value[_]): Opt[Value[V]] = ???

  /**
   * Method puts value by key. Key can't be null.
   *
   * @param key   key can't be null.
   * @param value value can be null.
   * @return previous value specified by the key or [[Opt.empty]] otherwise.
   * @throws DifferentKeyTypeException if the key is of a different type from the keys in the storage.
   */
  override def update[V, R](key: Value[_], value: Value[V]): Opt[Value[R]] = ???

  /**
   * Method removes value from space by the key.
   *
   * @param key key can't be null.
   * @return previous value specified by the key or [[Opt.empty]] otherwise.
   */
  override def remove[R](key: Value[_]): Opt[Value[R]] = ???

  override def clear(): Unit = ???
}
