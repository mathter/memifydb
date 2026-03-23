package io.github.mathter.memifydb.log;

import io.github.mathter.memifydb.core.command.Command;

import java.util.List;
import java.util.UUID;

public class Package {
    private final UUID transactionId;

    private final List<Command> commands;

    public Package(UUID transactionId, Command command) {
        this(transactionId, List.of(command));
    }

    @SuppressWarnings("unchecked")
    public Package(UUID transactionId, List<? extends Command> commands) {
        this.transactionId = transactionId;
        this.commands = (List<Command>) commands;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
