package io.github.mathter.memifydb.command;

import io.github.mathter.memifydb.common.util.ByteArray;

import javax.transaction.xa.Xid;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class IOUtil {
    public static SequenceNumber readSequenceNumber(InputStream is) throws IOException {
        return new SequenceNumber(ByteArray.readIntRaw(is));
    }

    public static void write(OutputStream os, SequenceNumber sequenceNumber) throws IOException {
        ByteArray.writeIntRaw(os, sequenceNumber.intValue());
    }

    public static SequenceNumber readSequenceNumber(ReadableByteChannel channel) throws IOException {
        final ByteBuffer buf = read(channel, Integer.BYTES);

        return new SequenceNumber(buf.getInt());
    }

    public static byte[] write(SequenceNumber sequenceNumber) throws IOException {
        final byte[] buf = new byte[Integer.BYTES];
        ByteArray.setIntRaw(buf, 0, sequenceNumber.intValue());

        return buf;
    }

    public static Xid readXid(InputStream is) throws IOException {
        return io.github.mathter.memifydb.common.xa.Xid.of(ByteArray.readIntRaw(is), IOUtil.read(is), IOUtil.read(is));
    }

    public static void write(OutputStream os, Xid xid) throws IOException {
        final byte[] globalTransactionId = xid.getGlobalTransactionId();
        final byte[] branchQualifier = xid.getBranchQualifier();

        ByteArray.writeIntRaw(os, xid.getFormatId());
        IOUtil.write(os, globalTransactionId);
        IOUtil.write(os, branchQualifier);
    }

    public static Xid readXid(ReadableByteChannel channel) throws IOException {
        return io.github.mathter.memifydb.common.xa.Xid.of(
                IOUtil.read(channel, 4).getInt(),
                IOUtil.read(channel),
                IOUtil.read(channel)
        );
    }

    public static byte[] read(InputStream is) throws IOException {
        final int length = ByteArray.readIntRaw(is);
        final byte[] buf = new byte[length];

        is.readNBytes(buf, 0, length);

        return buf;
    }

    public static void write(OutputStream os, byte[] buf) throws IOException {
        ByteArray.writeIntRaw(os, buf.length);
        os.write(buf);
    }

    public static byte[] read(ReadableByteChannel channel) throws IOException {
        int readed;
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);

        do {
            readed = channel.read(buf);
        } while (readed >= 0 && buf.remaining() > 0);

        if (buf.remaining() > 0) {
            throw new EOFException("Can't read byte array! Channel is ended!");
        }

        buf.rewind();
        buf = ByteBuffer.allocate(buf.getInt());

        do {
            channel.read(buf);
        } while (readed >= 0 && buf.remaining() > 0);

        if (buf.remaining() > 0) {
            throw new EOFException("Can't read byte array! Channel is ended!");
        }

        return buf.array();
    }

    public static ByteBuffer read(ReadableByteChannel channel, int count) throws IOException {
        int readed;
        int remaining = 0;
        final ByteBuffer buf = ByteBuffer.allocate(count);

        do {
            readed = channel.read(buf);
        } while (readed >= 0 && (remaining = buf.remaining()) > 0);

        if (remaining == 0) {
            return buf.rewind();
        } else {
            throw new EOFException("Can't read byte array! Channel is ended!");
        }
    }

    private IOUtil() {
    }
}
