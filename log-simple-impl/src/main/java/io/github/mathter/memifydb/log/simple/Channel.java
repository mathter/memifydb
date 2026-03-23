package io.github.mathter.memifydb.log.simple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

class Channel implements GatheringByteChannel {
    private static final Logger LOGGER = Logger.getLogger(Channel.class.getName());

    private static final String FILE_PATTERN = "%s%s%s";

    private final Path root;

    private final String fileNamePrefix;

    private final String fileNamePostfix;

    private final int fileMaxSize;

    private Path path;

    private FileChannel channel;

    private int index;


    public Channel(Path root, String fileNamePrefix, String fileNamePostfix, int fileMaxSize, int index) {
        this.root = root.normalize();
        this.fileNamePrefix = fileNamePrefix;
        this.fileNamePostfix = fileNamePostfix;
        this.fileMaxSize = fileMaxSize;
        this.index = index;
    }

    private void check() throws IOException {
        if (this.channel != null) {
            if (this.channel.size() > this.fileMaxSize) {
                LOGGER.info(() -> String.format("The maximum size of file '%s' has been reached!", this.path));
                this.channel.close();
                this.path = null;
                this.channel = null;
                this.check();
            }
        } else {
            final String fileName = String.format(FILE_PATTERN, this.fileNamePrefix, System.currentTimeMillis(), this.fileNamePostfix);
            this.path = this.root.resolve(fileName);
            LOGGER.info(() -> String.format("Create new WAL file '%s'", this.path));

            this.channel = FileChannel.open(this.path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            final Header header = new Header(Header.SIGNATURE, ++this.index, System.currentTimeMillis());

            header.write(this.channel);
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        this.check();
        return this.channel.write(src);
    }

    @Override
    public boolean isOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        if (this.channel != null) {
            this.channel.close();
        }
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        this.check();
        return this.channel.write(srcs, offset, length);
    }

    @Override
    public long write(ByteBuffer[] srcs) throws IOException {
        this.check();
        return this.channel.write(srcs);
    }
}
