package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.xa.XaCommand;

import javax.transaction.xa.Xid;

public class XaCommitTransactionCommand implements XaCommand {
    private static final byte[] PREFIX = {0x0A, 0x0D};

    private final Xid xid;

    public XaCommitTransactionCommand(Xid xid) {
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
