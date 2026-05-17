package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.Command;
import io.github.mathter.memifydb.common.command.CommandDeserializer;
import io.github.mathter.memifydb.common.util.ByteArray;
import io.github.mathter.memifydb.common.xa.Xid;

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
                throw new IOException("There is no reader for command prefix='" + prefix + "'!");
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

            result = reader.read(this, channel);
        } else {
            result = null;
        }

        return result;
    }

    private XaStartTransactionCommand readXaStartTransactionCommand(InputStream is) throws IOException {
        return new XaStartTransactionCommand(
                Xid.of(ByteArray.readIntRaw(is), read(is), read(is))
        );
    }

    private XaStartTransactionCommand readXaStartTransactionCommand(ReadableByteChannel channel) throws IOException {
        final ByteBuffer xidFormatIdBuffer = ByteBuffer.allocate(4);

        if (channel.read(xidFormatIdBuffer) == 4) {
            xidFormatIdBuffer.rewind();
            return new XaStartTransactionCommand(
                    Xid.of(xidFormatIdBuffer.getInt(), read(channel), read(channel))
            );
        } else {
            throw new IOException("There is no reader for command XaStartTransactionCommand!");
        }
    }

    private XaEndTransactionCommand readXaEndTransactionCommand(InputStream is) throws IOException {
        return new XaEndTransactionCommand(
                Xid.of(ByteArray.readIntRaw(is), read(is), read(is))
        );
    }

    private XaEndTransactionCommand readXaEndTransactionCommand(ReadableByteChannel channel) throws IOException {
        final ByteBuffer xidFormatIdBuffer = ByteBuffer.allocate(4);

        if (channel.read(xidFormatIdBuffer) == 4) {
            xidFormatIdBuffer.rewind();
            return new XaEndTransactionCommand(
                    Xid.of(xidFormatIdBuffer.getInt(), read(channel), read(channel))
            );
        } else {
            throw new IOException("There is no reader for command XaStartTransactionCommand!");
        }
    }

    private XaPrepareTransactionCommand readXaPrepareTransactionCommand(InputStream is) throws IOException {
        return new XaPrepareTransactionCommand(
                Xid.of(ByteArray.readIntRaw(is), read(is), read(is))
        );
    }

    private XaPrepareTransactionCommand readXaPrepareTransactionCommand(ReadableByteChannel channel) throws IOException {
        final ByteBuffer xidFormatIdBuffer = ByteBuffer.allocate(4);

        if (channel.read(xidFormatIdBuffer) == 4) {
            xidFormatIdBuffer.rewind();
            return new XaPrepareTransactionCommand(
                    Xid.of(xidFormatIdBuffer.getInt(), read(channel), read(channel))
            );
        } else {
            throw new IOException("There is no reader for command XaStartTransactionCommand!");
        }
    }

    private XaCommitTransactionCommand readXaCommitTransactionCommand(InputStream is) throws IOException {
        return new XaCommitTransactionCommand(
                Xid.of(ByteArray.readIntRaw(is), read(is), read(is))
        );
    }

    private XaCommitTransactionCommand readXaCommitTransactionCommand(ReadableByteChannel channel) throws IOException {
        final ByteBuffer xidFormatIdBuffer = ByteBuffer.allocate(4);

        if (channel.read(xidFormatIdBuffer) == 4) {
            xidFormatIdBuffer.rewind();
            return new XaCommitTransactionCommand(
                    Xid.of(xidFormatIdBuffer.getInt(), read(channel), read(channel))
            );
        } else {
            throw new IOException("There is no reader for command XaStartTransactionCommand!");
        }
    }

    private XaRollbackTransactionCommand readXaRollbackTransactionCommand(InputStream is) throws IOException {
        return new XaRollbackTransactionCommand(
                Xid.of(ByteArray.readIntRaw(is), read(is), read(is))
        );
    }

    private XaRollbackTransactionCommand readXaRollbackTransactionCommand(ReadableByteChannel channel) throws IOException {
        final ByteBuffer xidFormatIdBuffer = ByteBuffer.allocate(4);

        if (channel.read(xidFormatIdBuffer) == 4) {
            xidFormatIdBuffer.rewind();
            return new XaRollbackTransactionCommand(
                    Xid.of(xidFormatIdBuffer.getInt(), read(channel), read(channel))
            );
        } else {
            throw new IOException("There is no reader for command XaStartTransactionCommand!");
        }
    }

    private XaWrapperCommand<?> readXaWrapperCommand(InputStream is) throws IOException {
        return new XaWrapperCommand<>(
                Xid.of(ByteArray.readIntRaw(is), read(is), read(is)),
                this.deserialize(is)
        );
    }

    private XaWrapperCommand<?> readXaWrapperCommand(ReadableByteChannel channel) throws IOException {
        final ByteBuffer xidFormatIdBuffer = ByteBuffer.allocate(4);
        if (channel.read(xidFormatIdBuffer) == 4) {
            xidFormatIdBuffer.rewind();
            return new XaWrapperCommand<>(
                    Xid.of(xidFormatIdBuffer.getInt(), read(channel), read(channel)),
                    this.deserialize(channel)
            );
        } else {
            throw new IOException("There is no reader for command XaWrapperCommand!");
        }
    }

    private PutCommand readPutCommand(InputStream is) throws IOException {
        return new PutCommand(read(is), read(is), read(is));
    }

    private PutCommand readPutCommand(ReadableByteChannel channel) throws IOException {
        return new PutCommand(read(channel), read(channel), read(channel));
    }

    private RemoveCommand readRemoveCommand(InputStream is) throws IOException {
        return new RemoveCommand(read(is), read(is));
    }

    private RemoveCommand readRemoveCommand(ReadableByteChannel channel) throws IOException {
        return new RemoveCommand(read(channel), read(channel));
    }

    private static byte[] read(ReadableByteChannel channel) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);

        do {
            channel.read(buf);
        } while (buf.position() < buf.capacity());
        buf.rewind();
        buf = ByteBuffer.allocate(buf.getInt());

        do {
            channel.read(buf);
        } while (buf.position() < buf.capacity());

        return buf.array();
    }

    private static byte[] read(InputStream is) throws IOException {
        final int length = ByteArray.readIntRaw(is);
        final byte[] buf = new byte[length];

        is.readNBytes(buf, 0, length);

        return buf;
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
                result = this.prefix[0] == another.prefix[0]
                        && this.prefix[1] == another.prefix[1];
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
