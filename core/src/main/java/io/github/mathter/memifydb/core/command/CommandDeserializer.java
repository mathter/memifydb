package io.github.mathter.memifydb.core.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public interface CommandDeserializer {
    public Command deserialize(InputStream is) throws IOException;

    public Command deserialize(ReadableByteChannel channel) throws IOException;
}
