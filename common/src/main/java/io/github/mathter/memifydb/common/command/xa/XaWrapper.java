package io.github.mathter.memifydb.common.command.xa;

import io.github.mathter.memifydb.common.command.Command;

public interface XaWrapper<C extends Command> extends XaCommand {
    public static byte[] getPrefix() {
        throw new UnsupportedOperationException();
    }
    
    public C getCommand();
}
