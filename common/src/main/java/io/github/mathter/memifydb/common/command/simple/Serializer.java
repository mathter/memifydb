package io.github.mathter.memifydb.common.command.simple;

import io.github.mathter.memifydb.common.command.Command;
import io.github.mathter.memifydb.common.command.CommandSerializer;
import io.github.mathter.memifydb.common.command.PutCommand;
import io.github.mathter.memifydb.common.command.RemoveCommand;
import io.github.mathter.memifydb.common.util.ByteArray;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
