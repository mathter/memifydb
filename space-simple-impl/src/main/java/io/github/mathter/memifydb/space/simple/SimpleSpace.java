package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.core.data.Value;
import io.github.mathter.memifydb.core.util.Opt;
import io.github.mathter.memifydb.space.DifferentKeyTypeException;
import io.github.mathter.memifydb.space.KeyValueSpace;
import io.github.mathter.memifydb.space.UnsupportedKeyException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SimpleSpace implements KeyValueSpace {
    private final UUID id;

    private final String name;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = this.readWriteLock.readLock();

    private final Lock writeLock = this.readWriteLock.writeLock();

    private final Map<Comparable<?>, Record> map = new HashMap<>();

    public SimpleSpace(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Opt<Value> put(Value key, Value value) {
        final Opt<Value> result;
        final Object k = key.get();

        if (k instanceof Comparable<?> comparableKey) {
            this.writeLock.lock();

            try {
                final Record record = this.map.put(comparableKey, new Record(value));
                result = record != null ? Opt.of(record.value) : Opt.empty();
            } catch (ClassCastException e) {
                throw new DifferentKeyTypeException(e);
            } finally {
                this.writeLock.unlock();
            }
        } else {
            throw new UnsupportedKeyException(key);
        }

        return result;
    }

    @Override
    public Opt<Value> get(Value key) {
        final Opt<Value> result;
        final Object k = key.get();

        if (k instanceof Comparable<?> comparableKey) {
            this.readLock.lock();

            try {
                final Record record = this.map.get(comparableKey);
                result = record != null ? Opt.of(record.value) : Opt.empty();
            } catch (ClassCastException e) {
                throw new DifferentKeyTypeException(e);
            } finally {
                this.readLock.unlock();
            }
        } else {
            throw new UnsupportedKeyException(key);
        }

        return result;
    }

    @Override
    public Opt<Value> remove(Value key) {
        final Opt<Value> result;
        final Object k = key.get();

        if (k instanceof Comparable<?> comparableKey) {
            this.writeLock.lock();
            try {
                final Record record = this.map.remove(comparableKey);
                result = record != null ? Opt.of(record.value) : Opt.empty();
            } catch (ClassCastException e) {
                throw new DifferentKeyTypeException(e);
            } finally {
                this.writeLock.unlock();
            }
        } else {
            throw new UnsupportedKeyException(key);
        }

        return result;
    }

    class Record {
        final Value value;

        public Record(Value value) {
            this.value = value;
        }
    }
}
