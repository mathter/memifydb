package io.github.mathter.memifydb.space;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.util.Opt;

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
 * Key-Value space.
 */
public interface KeyValueOperations extends Operations {
    /**
     * Method puts value by key. Key can't be null.
     *
     * @param key   key can't be null.
     * @param value value can be null.
     * @return previous value specified by the key or {@linkplain Opt#empty()} otherwise.
     * @throws DifferentKeyTypeException if the key is of a different type from the keys in the storage.
     */
    public Opt<Value> put(Value key, Value value) throws DifferentKeyTypeException;

    /**
     * Method returns the value associated with the specified key.
     *
     * @param key key can't be null.
     * @return Opt wrapper of value.
     * @throws DifferentKeyTypeException if the key is of a different type from the keys in the storage.
     */
    public Opt<Value> get(Value key) throws DifferentKeyTypeException;

    /**
     * Method removes value from space by the key.
     *
     * @param key key can't be null.
     * @return previous value specified by the key or {@linkplain Opt#empty()} otherwise.
     * @throws DifferentKeyTypeException
     */
    public Opt<Value> remove(Value key) throws DifferentKeyTypeException;
}
