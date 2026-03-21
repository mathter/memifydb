package io.github.mathter.memifydb.log.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.CommandSerializationFactory;
import io.github.mathter.memifydb.core.util.ByteArray;
import io.github.mathter.memifydb.log.Log;
import io.github.mathter.memifydb.log.Package;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

class FileLog implements Log {
    private static final String FILE_PATTERN = "%s%s%s";

    private final byte[] buf = new byte[8];

    private final File root;

    private final String fileNamePrefix;

    private final String fileNamePostfix;

    private final int fileMaxSize;

    private final OutputStreamW os = new OutputStreamW();

    private final CommandSerializer serializer;

    private final CommandDeserializer deserializer;

    public FileLog(CommandSerializationFactory commandSerializationFactory, File root, String fileNamePrefix, String fileNamePostfix, int fileMaxSize) {
        this.root = root;
        this.fileNamePrefix = fileNamePrefix;
        this.fileNamePostfix = fileNamePostfix;
        this.fileMaxSize = fileMaxSize;
        this.serializer = commandSerializationFactory.serializer();
        this.deserializer = commandSerializationFactory.deserializer();
    }

    @Override
    public void log(Package pack) throws IOException {
        os.check();
        ByteArray.writeLongRaw(this.os, System.currentTimeMillis());

        final List<Command> commands = pack.getCommands();
        ByteArray.writeLongRaw(this.os, commands.size());


        for (Command command : commands) {
            this.serializer.serialize(this.os, command);
        }

        os.flush();
    }

    @Override
    public Stream<Command> get(long fromTimestamp) {
        throw new UnsupportedOperationException();
    }

    class OutputStreamW extends OutputStream {
        private File current;

        private int currentSize = 0;

        private OutputStream currentOutputStream;

        private void check() throws IOException {
            if (this.currentSize > FileLog.this.fileMaxSize) {
                this.currentOutputStream.flush();
                this.currentOutputStream.close();
                this.current = null;
                this.currentOutputStream = null;
                this.currentSize = 0;
                this.check();
            } else if (this.currentOutputStream == null) {
                final String pathName = String.format(
                        FILE_PATTERN,
                        FileLog.this.fileNamePrefix,
                        System.currentTimeMillis(),
                        FileLog.this.fileNamePostfix
                );
                this.current = new File(FileLog.this.root, pathName);
                this.currentOutputStream = new FileOutputStream(this.current);
                this.currentSize = 0;
            }
        }

        @Override

        public void write(int b) throws IOException {
            this.currentOutputStream.write(b);
            this.currentSize++;
        }

        @Override
        public void close() throws IOException {
            this.currentOutputStream.close();
        }

        @Override
        public void flush() throws IOException {
            this.currentOutputStream.flush();
        }
    }
}
