package io.github.mathter.memifydb.universe.simple.impl;

import io.github.mathter.memifydb.space.Space;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collection;

public class XaResource implements javax.transaction.xa.XAResource {
    private static final Cleaner CLEANER = Cleaner.create();

    private final SimpleUniverse universe;

    public XaResource(SimpleUniverse universe) {
        this.universe = universe;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        for (Space<?> space : this.universe.getSpaces()) {
            space.xaResource().commit(xid, onePhase);
        }
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        for (Space<?> space : this.universe.getSpaces()) {
            space.xaResource().end(xid, flags);
        }
    }

    @Override
    public void forget(Xid xid) throws XAException {
        for (Space<?> space : this.universe.getSpaces()) {
            space.xaResource().forget(xid);
        }
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        throw new XAException(XAException.XAER_INVAL);
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        return this == xares;
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        int result = XAResource.XA_OK;

        for (Space<?> space : this.universe.getSpaces()) {
            final int status = space.xaResource().prepare(xid);

            if (status != XAResource.XA_OK) {
                result = status;
            }
        }

        return result;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        final Collection<Xid> result = new ArrayList<>();
        for (Space<?> space : this.universe.getSpaces()) {
            final Xid[] xids = space.xaResource().recover(flag);

            if (xids != null && xids.length > 0) {
                for (Xid xid : xids) {
                    result.add(xid);
                }
            }
        }

        return result.toArray(cnt -> new Xid[cnt]);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        for (Space<?> space : this.universe.getSpaces()) {
            space.xaResource().rollback(xid);
        }
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        throw new XAException(XAException.XAER_INVAL);
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        for (Space<?> space : this.universe.getSpaces()) {
            space.xaResource().start(xid, flags);
        }
    }
}
