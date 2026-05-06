package io.github.mathter.memifydb.space;

import io.github.mathter.memifydb.transaction.xa.XaResourceProvider;

import java.util.UUID;

/**
 * Interface of the space. Its contains data stored in a database. It is like as table in a RDBMS.
 */
public interface Space<T extends Operations> extends OperationsProvider<T>, AutoCloseable {
    /**
     * Unique identifier of the space.
     *
     * @return unique identifier.
     */
    public UUID id();

    /**
     * Name of the space. It is like as table name on a RDBMS.
     *
     * @return name of the store.
     */
    public String name();

    /**
     *
     * @param <R> {@linkplain XaResourceProvider} subclass.
     * @return return xa resource is implementation of {@linkplain XaResourceProvider}.
     * @throws IllegalStateException if Space is closed.
     */
    public <R extends XaResourceProvider<T>> R xaResource() throws IllegalStateException;

    public boolean isClosed();
}

