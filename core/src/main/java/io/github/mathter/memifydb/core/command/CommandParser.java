package io.github.mathter.memifydb.core.command;

import java.io.InputStream;

public interface CommandParser {
    public Command paseNext(InputStream is);
}
