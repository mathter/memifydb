package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.Command;
import io.github.mathter.memifydb.common.command.xa.XaCommand;

import javax.transaction.xa.Xid;

public class XaWrapperCommand<C extends Command> implements XaCommand {
    private static final byte[] PREFIX = {0x0A, 0x00};

    private final Xid xid;

    private final C command;

    public XaWrapperCommand(Xid xid, C command) {
        this.xid = xid;
        this.command = command;
    }

    public static byte[] getPrefix() {
        return PREFIX;
    }

    @Override
    public Xid getXid() {
        return this.xid;
    }

    public C getCommand() {
        return this.command;
    }
}
