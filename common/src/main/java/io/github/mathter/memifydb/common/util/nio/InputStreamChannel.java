package io.github.mathter.memifydb.common.util.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InputStreamChannel implements ReadableByteChannel {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final InputStream is;

    private boolean closed = false;

    public InputStreamChannel(InputStream is) {
        this.is = Objects.requireNonNull(is);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        final int result;

        this.lock.writeLock().lock();
        try {
            if (this.closed) {
                throw new ClosedChannelException();
            } else {
                final byte[] buffer = new byte[dst.remaining()];

                if ((result = is.read(buffer)) >= 0) {
                    dst.put(buffer);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }

        return result;
    }

    @Override
    public boolean isOpen() {
        return !this.closed;
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.lock.writeLock().lock();
            try {
                this.closed = true;
            } finally {
                this.lock.writeLock().unlock();
            }
        }
    }
}
