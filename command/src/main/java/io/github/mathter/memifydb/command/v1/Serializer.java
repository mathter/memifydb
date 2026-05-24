package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.CommandSerializer;
import io.github.mathter.memifydb.command.IOUtil;
import io.github.mathter.memifydb.command.SequenceNumber;
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
    private static boolean write(OutputStream os, XaStartTransactionCommand command) throws IOException {
        os.write(XaStartTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, command.getXid());
        ByteArray.writeIntRaw(os, command.getFlags());

        return true;
    }

    private static ByteBuffer write(XaStartTransactionCommand command) throws IOException {
        final byte[] prefix = XaStartTransactionCommand.getPrefix();
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + SequenceNumber.BYTES
                        + Integer.BYTES
                        + Integer.BYTES
                        + globalTransactionId.length
                        + Integer.BYTES
                        + branchQualifier.length
                        + Integer.BYTES
        );

        buf.put(prefix);
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);
        buf.putInt(command.getFlags());

        return buf;
    }

    private static boolean write(OutputStream os, XaEndTransactionCommand command) throws IOException {
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();

        os.write(XaEndTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        ByteArray.writeIntRaw(os, xid.getFormatId());
        IOUtil.write(os, globalTransactionId);
        IOUtil.write(os, branchQualifier);
        ByteArray.writeIntRaw(os, command.getFlags());

        return true;
    }

    private static ByteBuffer write(XaEndTransactionCommand command) throws IOException {
        final byte[] prefix = XaEndTransactionCommand.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(command.getSequenceNumber());
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + Integer.BYTES
                        + globalTransactionId.length
                        + Integer.BYTES
                        + branchQualifier.length
                        + Integer.BYTES
        );

        buf.put(prefix);
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);
        buf.putInt(command.getFlags());

        return buf;
    }

    private static boolean write(OutputStream os, XaPrepareTransactionCommand command) throws IOException {
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();

        os.write(XaPrepareTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        ByteArray.writeIntRaw(os, xid.getFormatId());
        IOUtil.write(os, globalTransactionId);
        IOUtil.write(os, branchQualifier);

        return true;
    }

    private static ByteBuffer write(XaPrepareTransactionCommand command) throws IOException {
        final byte[] prefix = XaPrepareTransactionCommand.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(command.getSequenceNumber());
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + Integer.BYTES
                        + globalTransactionId.length
                        + Integer.BYTES
                        + branchQualifier.length
        );

        buf.put(prefix);
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);

        return buf;
    }

    private static boolean write(OutputStream os, XaCommitTransactionCommand command) throws IOException {
        os.write(XaCommitTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, command.getXid());

        return true;
    }

    private static ByteBuffer write(XaCommitTransactionCommand command) throws IOException {
        final byte[] prefix = XaCommitTransactionCommand.getPrefix();
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + SequenceNumber.BYTES
                        + Integer.BYTES
                        + Integer.BYTES
                        + globalTransactionId.length
                        + Integer.BYTES
                        + branchQualifier.length
        );

        buf.put(prefix);
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);

        return buf;
    }

    private static boolean write(OutputStream os, XaRollbackTransactionCommand command) throws IOException {
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();

        os.write(XaRollbackTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        ByteArray.writeIntRaw(os, xid.getFormatId());
        IOUtil.write(os, globalTransactionId);
        IOUtil.write(os, branchQualifier);

        return true;
    }

    private static ByteBuffer write(XaRollbackTransactionCommand command) throws IOException {
        final byte[] prefix = XaRollbackTransactionCommand.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(command.getSequenceNumber());
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + Integer.BYTES
                        + globalTransactionId.length
                        + Integer.BYTES
                        + branchQualifier.length
        );

        buf.put(prefix);
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);

        return buf;
    }

    private boolean write(OutputStream os, XaWrapperCommand<?> command) throws IOException {
        final Xid xid = command.getXid();
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();

        os.write(XaWrapperCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        ByteArray.writeIntRaw(os, xid.getFormatId());
        IOUtil.write(os, globalTransactionId);
        IOUtil.write(os, branchQualifier);
        this.serialize(os, command.getCommand());

        return true;
    }

    private ByteBuffer write(XaWrapperCommand<?> command) throws IOException {
        final Xid xid = command.getXid();
        final byte[] prefix = XaWrapperCommand.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(command.getSequenceNumber());
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();
        final ByteBuffer wrapped = this.serialize(command.getCommand()).rewind();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + Integer.BYTES
                        + globalTransactionId.length
                        + Integer.BYTES
                        + branchQualifier.length
                        + wrapped.remaining()
        );

        buf.put(XaWrapperCommand.getPrefix());
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(xid.getFormatId());
        buf.putInt(globalTransactionId.length);
        buf.put(globalTransactionId);
        buf.putInt(branchQualifier.length);
        buf.put(branchQualifier);
        buf.put(wrapped);

        return buf;
    }

    private static boolean write(OutputStream os, PutCommand command) throws IOException {
        os.write(PutCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, command.getRawSpaceName());
        IOUtil.write(os, command.getRawKey());
        IOUtil.write(os, command.getRawValue());

        return true;
    }

    private static ByteBuffer write(PutCommand command) throws IOException {
        final byte[] prefix = PutCommand.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(command.getSequenceNumber());
        final byte[] spaceName = command.getRawSpaceName();
        final byte[] key = command.getRawKey();
        final byte[] value = command.getRawValue();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + spaceName.length
                        + Integer.BYTES
                        + key.length
                        + Integer.BYTES
                        + value.length
        );

        buf.put(prefix);
        buf.put(IOUtil.write(command.getSequenceNumber()));
        buf.putInt(spaceName.length);
        buf.put(spaceName);
        buf.putInt(key.length);
        buf.put(key);
        buf.putInt(value.length);
        buf.put(value);

        return buf;
    }

    private static boolean write(OutputStream os, RemoveCommand command) throws IOException {
        os.write(RemoveCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, command.getRawSpaceName());
        IOUtil.write(os, command.getRawKey());

        return true;
    }

    private static ByteBuffer write(RemoveCommand command) throws IOException {
        final byte[] prefix = RemoveCommand.getPrefix();
        final byte[] sequenceNumberBuf = IOUtil.write(command.getSequenceNumber());
        final byte[] spaceName = command.getRawSpaceName();
        final byte[] key = command.getRawKey();
        final ByteBuffer buf = ByteBuffer.allocate(
                prefix.length
                        + sequenceNumberBuf.length
                        + Integer.BYTES
                        + spaceName.length
                        + Integer.BYTES
                        + key.length
        );

        buf.put(RemoveCommand.getPrefix());
        buf.put(sequenceNumberBuf);
        buf.putInt(spaceName.length);
        buf.put(spaceName);
        buf.putInt(key.length);
        buf.put(key);

        return buf;
    }

    @Override
    public boolean serialize(OutputStream os, Command command) throws IOException {
        return switch (command) {
            case XaStartTransactionCommand cmd -> write(os, cmd);
            case XaEndTransactionCommand cmd -> write(os, cmd);
            case XaPrepareTransactionCommand cmd -> write(os, cmd);
            case XaCommitTransactionCommand cmd -> write(os, cmd);
            case XaRollbackTransactionCommand cmd -> write(os, cmd);
            case XaWrapperCommand<?> cmd -> write(os, cmd);
            case PutCommand cmd -> write(os, cmd);
            case RemoveCommand cmd -> write(os, cmd);
            default -> false;
        };
    }

    @Override
    public ByteBuffer serialize(Command command) throws IOException {
        return (switch (command) {
            case XaStartTransactionCommand cmd -> write(cmd);
            case XaEndTransactionCommand cmd -> write(cmd);
            case XaPrepareTransactionCommand cmd -> write(cmd);
            case XaCommitTransactionCommand cmd -> write(cmd);
            case XaRollbackTransactionCommand cmd -> write(cmd);
            case XaWrapperCommand<?> cmd -> write(cmd);
            case PutCommand cmd -> write(cmd);
            case RemoveCommand cmd -> write(cmd);
            default -> null;
        }).rewind();
    }
}
