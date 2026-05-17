package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.Command;
import io.github.mathter.memifydb.common.command.CommandSerializer;
import io.github.mathter.memifydb.common.util.ByteArray;

import javax.transaction.xa.Xid;
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

    private boolean writeXaCommand(OutputStream os, XaWrapper<?> command) throws IOException {
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();

        os.write(XaWrapper.getPrefix());
        ByteArray.writeIntRaw(os, xid.getFormatId());
        write(os, globalTransactionId);
        write(os, branchQualifier);
        this.serialize(os, command.getCommand());

        return true;
    }

    private ByteBuffer write(XaWrapper<?> command) {
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer wrapped = this.serialize(command.getCommand()).rewind();
        final ByteBuffer buf = ByteBuffer.allocate(2 + 4 + 4 + globalTransactionId.length + 4 + branchQualifier.length + wrapped.remaining());

        buf.put(XaWrapper.getPrefix());
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);
        buf.put(wrapped);

        return buf;
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
        final ByteBuffer buf = ByteBuffer.allocate(2 + 4 + spaceName.length + 4 + key.length + 4 + value.length);

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
        final ByteBuffer buf = ByteBuffer.allocate(2 + 4 + spaceName.length + 4 + key.length);

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
            case XaWrapper<?> cmd -> writeXaCommand(os, cmd);
            case PutCommand cmd -> writePutCommand(os, cmd);
            case RemoveCommand cmd -> writeRemoveCommand(os, cmd);
            default -> false;
        };
    }

    @Override
    public ByteBuffer serialize(Command command) {
        return switch (command) {
            case XaWrapper<?> cmd -> write(cmd);
            case PutCommand cmd -> write(cmd).rewind();
            case RemoveCommand cmd -> write(cmd).rewind();
            default -> null;
        };
    }
}
