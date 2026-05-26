package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.CommandSerializer;
import io.github.mathter.memifydb.command.IOUtil;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.util.ByteArray;

import javax.transaction.xa.Xid;
import java.io.IOException;
import java.io.OutputStream;

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
class CommandSerializerV1 implements CommandSerializer {
    private final ValueSerializer valueSerializer;

    private final ValueDeserializer valueDeserializer;

    public CommandSerializerV1(ValueSerializer valueSerializer, ValueDeserializer valueDeserializer) {
        this.valueSerializer = valueSerializer;
        this.valueDeserializer = valueDeserializer;
    }

    private static boolean write(OutputStream os, XaStartTransactionCommand command) throws IOException {
        os.write(XaStartTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, command.getXid());
        ByteArray.writeIntRaw(os, command.getFlags());

        return true;
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

    private static boolean write(OutputStream os, XaCommitTransactionCommand command) throws IOException {
        os.write(XaCommitTransactionCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, command.getXid());

        return true;
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

    private boolean write(OutputStream os, PutCommand command) throws IOException {
        os.write(PutCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, this.valueSerializer.serialize(command.getSpaceName()));
        IOUtil.write(os, command.getKey().getRaw());
        IOUtil.write(os, command.getValue().getRaw());

        return true;
    }

    private boolean write(OutputStream os, RemoveCommand command) throws IOException {
        os.write(RemoveCommand.getPrefix());
        IOUtil.write(os, command.getSequenceNumber());
        IOUtil.write(os, this.valueSerializer.serialize(command.getSpaceName()));
        IOUtil.write(os, command.getKey().getRaw());

        return true;
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
}
