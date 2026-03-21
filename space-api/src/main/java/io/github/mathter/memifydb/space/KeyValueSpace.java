package io.github.mathter.memifydb.space;

import io.github.mathter.memifydb.core.data.Value;
import io.github.mathter.memifydb.core.util.Opt;

/**
 * Key-Value space.
 */
public interface KeyValueSpace extends Space {
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
