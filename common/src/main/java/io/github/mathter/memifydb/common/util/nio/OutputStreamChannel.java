package io.github.mathter.memifydb.common.util.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OutputStreamChannel implements WritableByteChannel {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final OutputStream os;

    private boolean closed;

    public OutputStreamChannel(OutputStream os) {
        this.os = Objects.requireNonNull(os);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        final byte[] buffer = new byte[src.remaining()];
        src.get(buffer);

        this.lock.writeLock().lock();

        try {
            this.os.write(buffer);
            return buffer.length;
        }finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isOpen() {
        this.lock.readLock().lock();

        try {
            return this.closed;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.lock.writeLock().lock();

        try {
            this.closed = true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
