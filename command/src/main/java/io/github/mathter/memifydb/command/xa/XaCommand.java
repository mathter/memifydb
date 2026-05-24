package io.github.mathter.memifydb.command.xa;

import io.github.mathter.memifydb.command.Command;

import javax.transaction.xa.Xid;

public interface XaCommand extends Command {
    public static byte[] getPrefix() {
        throw new UnsupportedOperationException();
    }

    public Xid getXid();
}
