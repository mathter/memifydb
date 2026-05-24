package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.CommandDeserializer;
import io.github.mathter.memifydb.command.IOUtil;
import io.github.mathter.memifydb.common.util.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
class Deserializer implements CommandDeserializer {
    private static final Map<Key, Reader> map;

    private static final Map<Key, ChannelReader> channelMap;

    static {
        map = Map.of(
                new Key(XaStartTransactionCommand.getPrefix()), Deserializer::readXaStartTransactionCommand,
                new Key(XaEndTransactionCommand.getPrefix()), Deserializer::readXaEndTransactionCommand,
                new Key(XaPrepareTransactionCommand.getPrefix()), Deserializer::readXaPrepareTransactionCommand,
                new Key(XaCommitTransactionCommand.getPrefix()), Deserializer::readXaCommitTransactionCommand,
                new Key(XaRollbackTransactionCommand.getPrefix()), Deserializer::readXaRollbackTransactionCommand,
                new Key(XaWrapperCommand.getPrefix()), Deserializer::readXaWrapperCommand,
                new Key(PutCommand.getPrefix()), Deserializer::readPutCommand,
                new Key(RemoveCommand.getPrefix()), Deserializer::readRemoveCommand
        );

        channelMap = Map.of(
                new Key(XaStartTransactionCommand.getPrefix()), Deserializer::readXaStartTransactionCommand,
                new Key(XaEndTransactionCommand.getPrefix()), Deserializer::readXaEndTransactionCommand,
                new Key(XaPrepareTransactionCommand.getPrefix()), Deserializer::readXaPrepareTransactionCommand,
                new Key(XaCommitTransactionCommand.getPrefix()), Deserializer::readXaCommitTransactionCommand,
                new Key(XaRollbackTransactionCommand.getPrefix()), Deserializer::readXaRollbackTransactionCommand,
                new Key(XaWrapperCommand.getPrefix()), Deserializer::readXaWrapperCommand,
                new Key(PutCommand.getPrefix()), Deserializer::readPutCommand,
                new Key(RemoveCommand.getPrefix()), Deserializer::readRemoveCommand
        );
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

    @Override
    public Command deserialize(ReadableByteChannel channel) throws IOException {
        final Command result;
        final ByteBuffer prefixBuffer = ByteBuffer.allocate(2);

        if (channel.read(prefixBuffer) > 0) {
            prefixBuffer.rewind();
            final byte[] prefix = prefixBuffer.array();
            final Key key = new Key(prefix);
            final ChannelReader reader = channelMap.get(key);

            if (reader != null) {
                result = reader.read(this, channel);
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

    private XaStartTransactionCommand readXaStartTransactionCommand(ReadableByteChannel channel) throws IOException {
        return new XaStartTransactionCommand(
                IOUtil.readSequenceNumber(channel),
                IOUtil.readXid(channel),
                IOUtil.read(channel, 4).getInt()
        );
    }

    private XaEndTransactionCommand readXaEndTransactionCommand(InputStream is) throws IOException {
        return new XaEndTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is),
                ByteArray.readIntRaw(is)
        );
    }

    private XaEndTransactionCommand readXaEndTransactionCommand(ReadableByteChannel channel) throws IOException {
        return new XaEndTransactionCommand(
                IOUtil.readSequenceNumber(channel),
                IOUtil.readXid(channel),
                IOUtil.read(channel, 4).getInt()
        );
    }

    private XaPrepareTransactionCommand readXaPrepareTransactionCommand(InputStream is) throws IOException {
        return new XaPrepareTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is)
        );
    }

    private XaPrepareTransactionCommand readXaPrepareTransactionCommand(ReadableByteChannel channel) throws IOException {
        return new XaPrepareTransactionCommand(
                IOUtil.readSequenceNumber(channel),
                IOUtil.readXid(channel)
        );
    }

    private XaCommitTransactionCommand readXaCommitTransactionCommand(InputStream is) throws IOException {
        return new XaCommitTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is)
        );
    }

    private XaCommitTransactionCommand readXaCommitTransactionCommand(ReadableByteChannel channel) throws IOException {
        return new XaCommitTransactionCommand(
                IOUtil.readSequenceNumber(channel),
                IOUtil.readXid(channel)
        );
    }

    private XaRollbackTransactionCommand readXaRollbackTransactionCommand(InputStream is) throws IOException {
        return new XaRollbackTransactionCommand(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is)
        );
    }

    private XaRollbackTransactionCommand readXaRollbackTransactionCommand(ReadableByteChannel channel) throws IOException {
        return new XaRollbackTransactionCommand(
                IOUtil.readSequenceNumber(channel),
                IOUtil.readXid(channel)
        );
    }

    private XaWrapperCommand<?> readXaWrapperCommand(InputStream is) throws IOException {
        return new XaWrapperCommand<>(
                IOUtil.readSequenceNumber(is),
                IOUtil.readXid(is),
                this.deserialize(is)
        );
    }

    private XaWrapperCommand<?> readXaWrapperCommand(ReadableByteChannel channel) throws IOException {
        return new XaWrapperCommand<>(
                IOUtil.readSequenceNumber(channel),
                IOUtil.readXid(channel),
                this.deserialize(channel)
        );
    }

    private PutCommand readPutCommand(InputStream is) throws IOException {
        return new PutCommand(IOUtil.readSequenceNumber(is), IOUtil.read(is), IOUtil.read(is), IOUtil.read(is));
    }

    private PutCommand readPutCommand(ReadableByteChannel channel) throws IOException {
        return new PutCommand(IOUtil.readSequenceNumber(channel), IOUtil.read(channel), IOUtil.read(channel), IOUtil.read(channel));
    }

    private RemoveCommand readRemoveCommand(InputStream is) throws IOException {
        return new RemoveCommand(IOUtil.readSequenceNumber(is), IOUtil.read(is), IOUtil.read(is));
    }

    private RemoveCommand readRemoveCommand(ReadableByteChannel channel) throws IOException {
        return new RemoveCommand(IOUtil.readSequenceNumber(channel), IOUtil.read(channel), IOUtil.read(channel));
    }

    private static class Key {
        final byte[] prefix;

        public Key(byte[] prefix) {
            this.prefix = prefix;
        }


        @Override
        public boolean equals(Object obj) {
            boolean result;

            if (obj instanceof Key another) {
                result = this.prefix[0] == another.prefix[0] && this.prefix[1] == another.prefix[1];
            } else {
                result = false;
            }

            return result;
        }

        @Override
        public int hashCode() {
            return this.prefix[0] ^ this.prefix[1];
        }

        @Override
        public String toString() {
            return Arrays.toString(this.prefix);
        }
    }

    @FunctionalInterface
    interface Reader {
        public Command read(Deserializer deserializer, InputStream is) throws IOException;
    }

    @FunctionalInterface
    interface ChannelReader {
        public Command read(Deserializer deserializer, ReadableByteChannel channel) throws IOException;
    }
}
