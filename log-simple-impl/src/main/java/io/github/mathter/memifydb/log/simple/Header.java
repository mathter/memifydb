package io.github.mathter.memifydb.log.simple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class Header {
    public static final byte[] SIGNATURE = "memifydbwal".getBytes(StandardCharsets.UTF_8);

    private final byte[] signature;

    private final int index;

    private final long timestamp;

    public Header(byte[] signature, int index, long timestamp) {
        this.signature = signature;
        this.index = index;
        this.timestamp = timestamp;
    }

    public byte[] getSignature() {
        return signature;
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static Header read(ReadableByteChannel channel) throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(SIGNATURE.length + Integer.BYTES + Long.BYTES);
        do {
            channel.read(buf);
        } while (buf.position() < buf.capacity());

        buf.rewind();
        final byte[] signature = new byte[SIGNATURE.length];
        buf.get(signature);
        if (!Arrays.equals(SIGNATURE, signature)) {
            throw new IllegalStateException(String.format("Invalid file signature! Signature is '%s'!", new String(signature)));
        }

        final int index = buf.getInt();
        final long timestamp = buf.getLong();

        return new Header(signature, index, timestamp);
    }

    public void write(WritableByteChannel channel) throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(SIGNATURE.length + Integer.BYTES + Long.BYTES);
        buf.put(SIGNATURE);
        buf.putInt(this.index);
        buf.putLong(this.timestamp);

        buf.rewind();
        channel.write(buf);
    }
}
