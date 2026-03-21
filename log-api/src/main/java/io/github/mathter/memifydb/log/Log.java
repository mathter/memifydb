package io.github.mathter.memifydb.log;

import io.github.mathter.memifydb.core.command.Command;

import java.io.IOException;
import java.util.stream.Stream;

public interface Log {
    public void log(Package pack) throws IOException;

    public Stream<Command> get(long fromTimestamp);
}
