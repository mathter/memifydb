package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.RemoveCommand;
import io.github.mathter.memifydb.core.util.ByteArray;

import java.io.IOException;
import java.io.OutputStream;

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

    private boolean writeRemoveCommand(OutputStream os, RemoveCommand command) throws IOException {
        os.write(RemoveCommand.getPrefix());
        write(os, command.getRawSpaceName());
        write(os, command.getRawKey());
        os.flush();

        return true;
    }

    @Override
    public boolean serialize(OutputStream os, Command command) throws IOException {
        return switch (command) {
            case PutCommand cmd -> writePutCommand(os, cmd);
            case RemoveCommand cmd -> writeRemoveCommand(os, cmd);
            default -> false;
        };
    }
}
