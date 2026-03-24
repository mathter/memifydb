package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.RemoveCommand;
import io.github.mathter.memifydb.core.util.ByteArray;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

class Deserializer implements CommandDeserializer {
    private static final Map<Byte, Reader> map;

    private static final Map<Byte, ChannelReader> channelMap;

    static {
        map = Map.of(
                PutCommand.getPrefix(), Deserializer::readPutCommand,
                RemoveCommand.getPrefix(), Deserializer::readRemoveCommand
        );

        channelMap = Map.of(
                PutCommand.getPrefix(), Deserializer::readPutCommand,
                RemoveCommand.getPrefix(), Deserializer::readRemoveCommand
        );
    }

    @Override
    public Command deserialize(InputStream is) throws IOException {
        final Command result;
        final int prefix = is.read();

        if (prefix >= 0) {
            final Reader reader = map.get((byte) prefix);

            if (reader != null) {
                result = reader.read(is);
            } else {
                throw new IOException("There is no reader for command prefix='" + (byte) prefix + "'!");
            }
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public Command deserialize(ReadableByteChannel channel) throws IOException {
        final Command result;
        final ByteBuffer prefixBuffer = ByteBuffer.allocate(1);

        if (channel.read(prefixBuffer) > 0) {
            prefixBuffer.rewind();
            final ChannelReader reader = channelMap.get(prefixBuffer.get());

            result = reader.read(channel);
        } else {
            result = null;
        }

        return result;
    }

    private static PutCommand readPutCommand(InputStream is) throws IOException {
        return new PutCommand(read(is), read(is), read(is));
    }

    private static PutCommand readPutCommand(ReadableByteChannel channel) throws IOException {
        return new PutCommand(read(channel), read(channel), read(channel));
    }

    private static RemoveCommand readRemoveCommand(InputStream is) throws IOException {
        return new RemoveCommand(read(is), read(is));
    }

    private static RemoveCommand readRemoveCommand(ReadableByteChannel channel) throws IOException {
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

    @FunctionalInterface
    interface Reader {
        public Command read(InputStream is) throws IOException;
    }

    @FunctionalInterface
    interface ChannelReader {
        public Command read(ReadableByteChannel channel) throws IOException;
    }
}
