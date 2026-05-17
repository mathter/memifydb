package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.xa.XaCommand;

import javax.transaction.xa.Xid;

public class XaPrepareTransactionCommand implements XaCommand {
    private static final byte[] PREFIX = {0x0A, 0x0C};

    private final Xid xid;

    public XaPrepareTransactionCommand(Xid xid) {
        this.xid = xid;
    }

    public static byte[] getPrefix() {
        return PREFIX;
    }

    @Override
    public Xid getXid() {
        return this.xid;
    }
}
