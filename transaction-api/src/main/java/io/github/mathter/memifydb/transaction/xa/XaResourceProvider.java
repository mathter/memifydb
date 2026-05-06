package io.github.mathter.memifydb.transaction.xa;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public interface XaResourceProvider<T> extends XAResource {
    public T xa(Xid xid) throws IllegalArgumentException, IllegalStateException;
}
