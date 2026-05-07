package io.github.mathter.memifydb.log.simple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
