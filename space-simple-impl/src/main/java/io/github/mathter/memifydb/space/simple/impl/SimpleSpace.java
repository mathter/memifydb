package io.github.mathter.memifydb.space.simple.impl;

import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.transaction.xa.XaResourceProvider;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
class SimpleSpace implements Space<KeyValueOperations> {
    private static final Logger LOG = Logger.getLogger(SimpleSpace.class.getName());

    private static final Cleaner CLEANER = Cleaner.create();

    private final SimpleOperations operations;

    private final UUID id;

    private final String name;

    private final XaResource xaResource;

    private boolean isClosed;

    public SimpleSpace(UUID id, String name, long transactionTimeout) {
        this.id = id;
        this.name = name;
        this.operations = new SimpleOperations();
        this.xaResource = new XaResource(this, transactionTimeout);
        this.isClosed = false;

        CLEANER.register(this, () -> {
            try {
                this.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, String.format("Error while closing Space id=%, name=%s", this.id, this.name), e);
            }
        });
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
    @SuppressWarnings("unchecked")
    public <R extends XaResourceProvider<KeyValueOperations>> R xaResource() {
        if (this.isClosed) {
            throw new IllegalStateException();
        } else {
            return (R) this.xaResource;
        }
    }

    @Override
    public KeyValueOperations operatons() {
        return this.operations;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public void close() throws Exception {
        Exception exception = null;
        try {
            this.operations.clear();
        } catch (Exception e) {
            exception = new IOException("Exception caught while trying to close the space.");
            exception.addSuppressed(e);
            LOG.log(
                    Level.SEVERE,
                    "Error ocurred while closing SimpleSpace id=" + this.id + ", name='" + this.name + "'!",
                    e
            );
        } finally {
            try {
                this.xaResource.close();
            } catch (Exception e) {
                if (exception == null) {
                    exception = new IOException("Exception caught while trying to close the space.");
                }
                exception.addSuppressed(e);

                LOG.log(
                        Level.SEVERE,
                        "Error ocurred while closing SimpleSpace id=" + this.id + ", name='" + this.name + "'!",
                        e
                );
            }

            this.isClosed = true;
        }
    }
}
