package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.IOUtil;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.util.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

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
class CommandDeserializerV1 implements io.github.mathter.memifydb.command.CommandDeserializer {
    private static final Map<Key, Reader> map;

    private final ValueSerializer valueSerializer;

    private final ValueDeserializer valueDeserializer;

    static {
        map = Map.of(
                new Key(XaStartTransactionCommand.getPrefix()), CommandDeserializerV1::readXaStartTransactionCommand,
                new Key(XaEndTransactionCommand.getPrefix()), CommandDeserializerV1::readXaEndTransactionCommand,
                new Key(XaPrepareTransactionCommand.getPrefix()), CommandDeserializerV1::readXaPrepareTransactionCommand,
                new Key(XaCommitTransactionCommand.getPrefix()), CommandDeserializerV1::readXaCommitTransactionCommand,
                new Key(XaRollbackTransactionCommand.getPrefix()), CommandDeserializerV1::readXaRollbackTransactionCommand,
                new Key(XaWrapperCommand.getPrefix()), CommandDeserializerV1::readXaWrapperCommand,
                new Key(PutCommand.getPrefix()), CommandDeserializerV1::readPutCommand,
                new Key(RemoveCommand.getPrefix()), CommandDeserializerV1::readRemoveCommand,
                new Key(GetCommand.getPrefix()), CommandDeserializerV1::readGetCommand,
                new Key(XaRecoverTransactionCommand.getPrefix()), CommandDeserializerV1::readXaRecoverCommand
        );
    }

    public CommandDeserializerV1(ValueSerializer valueSerializer, ValueDeserializer valueDeserializer) {
        this.valueSerializer = valueSerializer;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public Command deserialize(InputStream is) throws IOException {
        final Command result;
        final byte[] prefix = new byte[2];

        if (is.readNBytes(prefix, 0, 2) == 2) {
            final Reader reader = map.get(new Key(prefix));

            if (reader != null) {
                result = reader.read(this, is);
            } else {
                throw new IOException("There is no reader for command prefix='" + Arrays.toString(prefix) + "'!");
            }
        } else {
            result = null;
        }

        return result;
    }

    private XaStartTransactionCommand readXaStartTransactionCommand(InputStream is) throws IOException {
        return new XaStartTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is),
                ByteArray.readIntRaw(is)
        );
    }

    private XaEndTransactionCommand readXaEndTransactionCommand(InputStream is) throws IOException {
        return new XaEndTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is),
                ByteArray.readIntRaw(is)
        );
    }

    private XaPrepareTransactionCommand readXaPrepareTransactionCommand(InputStream is) throws IOException {
        return new XaPrepareTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is)
        );
    }

    private XaCommitTransactionCommand readXaCommitTransactionCommand(InputStream is) throws IOException {
        return new XaCommitTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is),
                is.readNBytes(1)[0] != 0
        );
    }

    private XaRollbackTransactionCommand readXaRollbackTransactionCommand(InputStream is) throws IOException {
        return new XaRollbackTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is)
        );
    }

    private XaWrapperCommand<?> readXaWrapperCommand(InputStream is) throws IOException {
        return new XaWrapperCommand<>(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is),
                this.deserialize(is)
        );
    }

    private XaRecoverTransactionCommand readXaRecoverCommand(InputStream is) throws IOException {
        return new XaRecoverTransactionCommand(
                IOUtil.readSequenceNumber(is),
                ByteArray.readIntRaw(is)
        );
    }

    private PutCommand readPutCommand(InputStream is) throws IOException {
        return new PutCommand(
                IOUtil.readSequenceNumber(is),
                this.valueDeserializer.deserialize(IOUtil.read(is)).get(),
                this.valueDeserializer.deserialize(IOUtil.read(is)),
                this.valueDeserializer.deserialize(IOUtil.read(is))
        );
    }

    private RemoveCommand readRemoveCommand(InputStream is) throws IOException {
        return new RemoveCommand(
                IOUtil.readSequenceNumber(is),
                this.valueDeserializer.deserialize(IOUtil.read(is)).get(),
                this.valueDeserializer.deserialize(IOUtil.read(is))
        );
    }

    private GetCommand readGetCommand(InputStream is) throws IOException {
        return new GetCommand(
                IOUtil.readSequenceNumber(is),
                this.valueDeserializer.deserialize(IOUtil.read(is)).get(),
                this.valueDeserializer.deserialize(IOUtil.read(is))
        );
    }

    @FunctionalInterface
    interface Reader {
        public Command read(CommandDeserializerV1 deserializer, InputStream is) throws IOException;
    }
}
