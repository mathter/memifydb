package io.github.mathter.memifydb.core.command;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface CommandSerializer {
    public boolean serialize(OutputStream os, Command command) throws IOException;

    public ByteBuffer serialize(Command command);
}
