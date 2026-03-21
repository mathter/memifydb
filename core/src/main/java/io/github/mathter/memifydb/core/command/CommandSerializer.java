package io.github.mathter.memifydb.core.command;

import java.io.IOException;
import java.io.OutputStream;

public interface CommandSerializer {
    public boolean serialize(OutputStream os, Command command) throws IOException;
}
