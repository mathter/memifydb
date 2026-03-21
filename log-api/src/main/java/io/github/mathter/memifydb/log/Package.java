package io.github.mathter.memifydb.log;

import io.github.mathter.memifydb.core.command.Command;

import java.util.List;

public class Package {
    private final List<Command> commands;

    public Package(Command command) {
        this(List.of(command));
    }

    @SuppressWarnings("unchecked")
    public Package(List<? extends Command> commands) {
        this.commands = (List<Command>) commands;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
