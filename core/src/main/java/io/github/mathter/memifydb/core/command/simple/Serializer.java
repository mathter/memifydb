package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.RemoveCommand;
import io.github.mathter.memifydb.core.util.ByteArray;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

class Serializer implements CommandSerializer {
    private static void write(OutputStream os, byte[] buf) throws IOException {
        ByteArray.writeIntRaw(os, buf.length);
        os.write(buf);
    }

    private boolean writePutCommand(OutputStream os, PutCommand command) throws IOException {
        os.write(PutCommand.getPrefix());
        write(os, command.getRawSpaceName());
        write(os, command.getRawKey());
        write(os, command.getRawValue());
        os.flush();

        return true;
    }

    private ByteBuffer write(PutCommand command) {
        final byte[] spaceName = command.getRawSpaceName();
        final byte[] key = command.getRawKey();
        final byte[] value = command.getRawValue();
        final ByteBuffer buf = ByteBuffer.allocate(1 + 4 + spaceName.length + 4 + key.length + 4 + value.length);

        buf.put(PutCommand.getPrefix());
        buf.putInt(spaceName.length);
        buf.put(spaceName);
        buf.putInt(key.length);
        buf.put(key);
        buf.putInt(value.length);
        buf.put(value);

        return buf;
    }

    private boolean writeRemoveCommand(OutputStream os, RemoveCommand command) throws IOException {
        os.write(RemoveCommand.getPrefix());
        write(os, command.getRawSpaceName());
        write(os, command.getRawKey());
        os.flush();

        return true;
    }

    private ByteBuffer write(RemoveCommand command) {
        final byte[] spaceName = command.getRawSpaceName();
        final byte[] key = command.getRawKey();
        final ByteBuffer buf = ByteBuffer.allocate(1 + 4 + spaceName.length + 4 + key.length);

        buf.put(RemoveCommand.getPrefix());
        buf.putInt(spaceName.length);
        buf.put(spaceName);
        buf.putInt(key.length);
        buf.put(key);

        return buf;
    }

    @Override
    public boolean serialize(OutputStream os, Command command) throws IOException {
        return switch (command) {
            case PutCommand cmd -> writePutCommand(os, cmd);
            case RemoveCommand cmd -> writeRemoveCommand(os, cmd);
            default -> false;
        };
    }

    @Override
    public ByteBuffer serialize(Command command) {
        return switch (command) {
            case PutCommand cmd -> write(cmd).rewind();
            case RemoveCommand cmd -> write(cmd).rewind();
            default -> null;
        };
    }
}
