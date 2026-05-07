package io.github.mathter.memifydb.space;

import io.github.mathter.memifydb.transaction.xa.XaResourceProvider;

import java.util.UUID;

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

