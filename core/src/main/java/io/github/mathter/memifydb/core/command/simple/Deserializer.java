package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.RemoveCommand;
import io.github.mathter.memifydb.core.util.ByteArray;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

class Deserializer implements CommandDeserializer {
    private static final Map<Byte, Reader> map;

    static {
        map = Map.of(
                PutCommand.getPrefix(), Deserializer::readPutCommand,
                RemoveCommand.getPrefix(), Deserializer::readRemoveCommand
        );
    }

    @Override
    public Command deserialize(InputStream is) throws IOException {
        final int prefix = is.read();

        if (prefix >= 0) {
            final Reader reader = map.get((byte) prefix);

            if (reader != null) {
                return reader.read(is);
            } else {
                throw new IOException("There is no reader for command prefix='" + (byte) prefix + "'!");
            }
        } else {
            throw new EOFException();
        }
    }

    private static PutCommand readPutCommand(InputStream is) throws IOException {
        return new PutCommand(read(is), read(is), read(is));
    }

    private static RemoveCommand readRemoveCommand(InputStream is) throws IOException {
        return new RemoveCommand(read(is), read(is));
    }

    private static byte[] read(InputStream is) throws IOException {
        final int length = ByteArray.readIntRaw(is);
        final byte[] buf = new byte[length];

        is.readNBytes(buf, 0, length);

        return buf;
    }

    @FunctionalInterface
    interface Reader {
        public Command read(InputStream is) throws IOException;
    }
}
