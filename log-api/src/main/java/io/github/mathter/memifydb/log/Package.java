package io.github.mathter.memifydb.log;

import io.github.mathter.memifydb.core.command.Command;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Package {
    private final UUID transactionId;

    private final Command[] commands;

    public Package(UUID transactionId, Command command) {
        this(transactionId, new Command[]{command});
    }

    public Package(UUID transactionId, Command[] commands) {
        this.transactionId = Objects.requireNonNull(transactionId);
        this.commands = commands;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Command[] getCommands() {
        return commands;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof Package another
                && Objects.equals(this.transactionId, another.transactionId)
                && Objects.deepEquals(this.commands, another.commands));
    }

    @Override
    public int hashCode() {
        return this.transactionId.hashCode();
    }
}
