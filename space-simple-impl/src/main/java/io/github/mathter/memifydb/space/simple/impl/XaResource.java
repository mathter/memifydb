package io.github.mathter.memifydb.space.simple.impl;


import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.util.Opt;
import io.github.mathter.memifydb.space.DifferentKeyTypeException;
import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.transaction.xa.XaException;
import io.github.mathter.memifydb.transaction.xa.XaResourceProvider;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
class XaResource implements XAResource, XaResourceProvider<KeyValueOperations>, AutoCloseable {
    private static final Logger LOG = Logger.getLogger(XaResource.class.getName());

    private static final long CLEANUP_INTERVAL_MILLISECONDS = 3000;

    private static final Cleaner CLEANER = Cleaner.create();

    private final Map<Xid, XaOperations> map = new ConcurrentHashMap<>();

    private final SimpleSpace space;

    private final Thread tread = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            final long now = System.currentTimeMillis();
            this.map
                    .values()
                    .stream()
                    .filter(op -> (now - op.created) >= this.transactionTimeout)
                    .forEach(op -> this.map.remove(op.xid));

            try {
                Thread.sleep(CLEANUP_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.info(String.format("Cleanup thread is interrupted! resource=%s", this));
            }
        }
    });

    private long transactionTimeout;

    private boolean isClosed;

    XaResource(SimpleSpace space, long transactionTimeout) {
        this.space = space;
        this.transactionTimeout = transactionTimeout;
        CLEANER.register(this, () -> {
            LOG.info("Cleaning up XAResource " + this);

            try {
                this.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, String.format("Error closing XAResource resource=%s", this), e);
            }
        });
        this.tread.start();
        this.isClosed = false;
    }

    @Override
    public KeyValueOperations xa(Xid xid) {
        LOG.finest(() -> String.format("get xa operations for xid=%s, resource=%s", xid, this));

        this.check(xid);

        if (xid == null) {
            throw new IllegalArgumentException(String.format("xid must be not null! resource=%s", this));
        }

        final XaOperations op = map.computeIfAbsent(xid, x -> new XaOperations(x));

        if (System.currentTimeMillis() - op.created >= this.transactionTimeout) {
            throw new IllegalStateException(
                    String.format("Transaction timed out! xid=%s, resource=%s", xid, this)
            );
        }

        return op;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        LOG.finest(() -> String.format("Committing XAResource for xid=%s, onePhase=%s, resource=%s", xid, onePhase, this));

        this.check(xid);

        final XaOperations op = this.getValidOperations(xid);

        if (onePhase && op.status == Status.ENDED || op.status == Status.PREPARED) {
            final KeyValueOperations operations = this.space.operatons();

            for (Map.Entry<Value, Opt<Value>> entry : op.map.entrySet()) {
                operations.put(entry.getKey(), entry.getValue().get());
            }
        } else {
            throw new XaException(String.format("Illegal state=%s, must be in state=%s, resource=%s", op.status, onePhase ? Status.ENDED : Status.PREPARED, this), XAException.XAER_RMERR);
        }
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        LOG.finest(() -> String.format("Ending XAResource for %s", xid));

        this.check(xid);

        final XaOperations op = this.getValidOperations(xid);

        if (op.status == Status.STARTED) {
            op.status = Status.ENDED;
        } else {
            throw new XaException(String.format("Illegal state=%s, must be in state=%s, resource=%s", this, op.status, Status.STARTED), XAException.XAER_RMERR);
        }
    }

    @Override
    public void forget(Xid xid) throws XAException {
        throw new XaException(String.format("Heuristic commit/rollback not supported. forget xid=%s", xid), XAException.XAER_NOTA);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        LOG.finest(() -> String.format("getTransactionTimeout for %s, resource=%s", this.transactionTimeout, this));

        this.check(null);

        return (int) this.transactionTimeout / 1000;
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        LOG.finest(() -> String.format("Checking XAResource for %s, this %s", xares, this));

        this.check(null);

        return this == xares;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        LOG.finest(() -> String.format("Preparing XAResource for xid=%s, resource=%s", xid));

        this.check(xid);

        final XaOperations op = map.get(xid);

        if (op == null) {
            throw new XaException(String.format("xid=%s not valid, resource=%s! There is no resource!", xid, this), XAException.XAER_RMERR);
        }

        op.status = Status.PREPARED;

        return op.map.isEmpty() ? XAResource.XA_RDONLY : XAResource.XA_OK;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        LOG.finest(() -> String.format("Recovering XAResource for %s, resource=%s", flag, this));

        this.check(null);

        final Xid[] result;

        if (flag != TMSTARTRSCAN && flag != TMENDRSCAN && flag != TMNOFLAGS && flag != (TMSTARTRSCAN | TMENDRSCAN)) {
            throw new XaException(String.format("Invalid flags=%s, resource=%s !", flag, this), XAException.XAER_INVAL);
        }

        if ((flag & TMSTARTRSCAN) == 0) {
            result = new Xid[0];
        } else {
            result = XaResource.this.map
                    .values()
                    .stream()
                    .filter(e -> Status.PREPARED == e.status)
                    .map(e -> e.xid)
                    .toArray(Xid[]::new);
        }

        return result;
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        LOG.finest(() -> String.format("Rolling back XAResource for xid=%s, resource=%s", xid, this));

        this.check(xid);

        final XaOperations op = map.get(xid);

        if (op == null) {
            throw new XaException(String.format("xid=%s not valid, resource=%s!", xid, this), XAException.XAER_RMERR);
        }

        op.status = Status.ROLLING_BACK;
        this.map.remove(xid);
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        LOG.finest(() -> String.format("Setting XAResource transaction timeout for %s, resource=%s, but operation not supported!", seconds, this));

        this.check(null);

        return false;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        LOG.finest(() -> String.format("Starting XAResource for xid=%s and flags=%s, resource=%s", xid, flags, this));

        this.check(xid);


        if (flags != XAResource.TMNOFLAGS && flags != XAResource.TMRESUME && flags != XAResource.TMJOIN) {
            throw new XaException(String.format("Invalid flags=%s, resource=%s !", flags, this), XAException.XAER_INVAL);
        }

        if (xid == null) {
            throw new XaException(String.format("xid must not be null! flags=%s, resource=%s", flags, this), XAException.XAER_INVAL);
        }

        final XaOperations op = map.get(xid);

        if (op == null) {
            throw new XaException(String.format("%s not valid! There is no resource! resource=%s", xid, this), XAException.XAER_RMERR);
        }

        if (op.status != Status.NEW) {
            throw new XaException(String.format("Resource xid=%s must be in status=%s, requested status=%s, resource=%s", xid, Status.NEW, op.status, this), XAException.XAER_PROTO);
        }

        if (flags == XAResource.TMRESUME) {
            throw new XaException(String.format("suspend/resume not implemented! xid=%s, flags=%s, resource=%s", xid, flags, this), XAException.XAER_RMERR);
        }

        if (flags == XAResource.TMJOIN) {
            if (op.status != Status.ENDED) {
                throw new XaException(
                        String.format(
                                "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid=%s, state=%s, flags=%s, resource=%s",
                                xid,
                                op.status,
                                flags,
                                this
                        ),
                        XAException.XAER_RMERR
                );
            }
        } else if (op.status == Status.ENDED) {
            throw new XaException(
                    String.format(
                            "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid=%s, state=%s, flags=%s, resource=%s",
                            xid,
                            op.status,
                            flags,
                            this
                    ),
                    XAException.XAER_RMERR
            );
        }

        op.status = Status.STARTED;
    }

    @Override
    public synchronized void close() throws Exception {
        if (!this.isClosed) {
            this.tread.interrupt();
            this.map.clear();
            this.isClosed = true;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("XaResource{");
        sb.append(SimpleSpace.class.getName()).append(", ");
        sb.append("name=").append(this.space.name());
        sb.append("id=").append(this.space.id());
        sb.append("}");

        return sb.toString();
    }

    private void check(Xid xid) throws IllegalStateException {
        if (this.isClosed) {
            throw new IllegalStateException(String.format("XAResource %s is already closed, xid=%s", this, xid));
        }
    }

    private XaOperations getValidOperations(Xid xid) throws XAException {
        final XaOperations op = map.get(xid);

        if (op == null) {
            throw new XaException(String.format("There is no resource for transaction with id=%s, resource=%s", xid, this), XAException.XAER_RMERR);
        }

        return op;
    }

    private class XaOperations implements KeyValueOperations {
        private final Xid xid;

        private final long created = System.currentTimeMillis();

        private final Map<Value, Opt<Value>> map = new HashMap<>();

        private Status status = Status.NEW;

        private XaOperations(Xid xid) {
            this.xid = xid;
        }

        @Override
        public Opt<Value> put(Value key, Value value) throws DifferentKeyTypeException {
            LOG.finest(() -> String.format("Putting key=%s with value=%s, resource=%s", key, value, XaResource.this));

            final Opt<Value> result;

            if (this.status == Status.STARTED) {
                result = this.map.put(key, Opt.of(value));
            } else {
                throw new IllegalStateException(
                        String.format(
                                "Can't put key %s with value %s, current state=%s must be %s. XAResource#start must be call first!",
                                key,
                                value,
                                this.status,
                                Status.STARTED
                        )
                );
            }

            return result;
        }

        @Override
        public Opt<Value> get(Value key) throws DifferentKeyTypeException {
            LOG.finest(() -> String.format("Getting value by key=%s, resource=%s", key, XaResource.this));
            return this.map.getOrDefault(key, Opt.empty());
        }

        @Override
        public Opt<Value> remove(Value key) throws DifferentKeyTypeException {
            LOG.finest(() -> String.format("Removing value by key=%s, resource=%s", key, XaResource.this));
            return this.map.remove(key);
        }

        @Override
        public void clear() throws Exception {

        }
    }

    private enum Status {
        NEW,

        STARTED,

        ENDED,

        PREPARED,

        COMMITTING,

        ROLLING_BACK,
    }
}
