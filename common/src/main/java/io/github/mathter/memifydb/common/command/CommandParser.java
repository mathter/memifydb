package io.github.mathter.memifydb.common.command;

import java.io.InputStream;

public interface CommandParser {
    public Command paseNext(InputStream is);
}
