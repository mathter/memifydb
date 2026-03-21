package io.github.mathter.memifydb.core.command;

import java.io.IOException;
import java.io.InputStream;

public interface CommandDeserializer {
    public Command deserialize(InputStream is) throws IOException;
}
