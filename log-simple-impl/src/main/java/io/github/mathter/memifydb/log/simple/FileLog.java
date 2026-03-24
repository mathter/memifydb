package io.github.mathter.memifydb.log.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializationFactory;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.log.Log;
import io.github.mathter.memifydb.log.Package;
import org.apache.commons.lang3.tuple.Pair;

import java.io.EOFException;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * File header:
 * | memifydbwal | fileindex (int 4 bytes) | creation timestamp (long 8 bytes) |
 */
class FileLog implements Log {
    private static final Logger LOG = Logger.getLogger(FileLog.class.getName());

    private static final Cleaner CLEANER = Cleaner.create();

    private static final byte[] PACKAGE_SIGNATURE = new byte[]{0x0B, 0x01};

    private static final String FILE_PATTERN = "%s%s%s";

    private final Pattern pattern;

    private final CommandSerializer serializer;

    private final CommandDeserializer deserializer;

    private final Path root;

    private final Channel channel;

    private final Cleaner.Cleanable cleanable;

    public FileLog(CommandSerializationFactory commandSerializationFactory,
                   Path root,
                   String fileNamePrefix,
                   String fileNamePostfix,
                   int fileMaxSize) {
        this.root = root;
        this.serializer = commandSerializationFactory.serializer();
        this.deserializer = commandSerializationFactory.deserializer();
        this.pattern = Pattern.compile(
                String.format("^(%s)\\d+(%s)$", fileNamePrefix, fileNamePostfix)
        );

        try {
            this.channel = new Channel(root, fileNamePrefix, fileNamePostfix, fileMaxSize, lastIndex(root));
            this.cleanable = CLEANER.register(this, () -> {
                try {
                    LOG.info("Close channel" + this.channel);
                    this.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Error were occurred while file logger closing!");
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Can't create log!", e);
        }
    }

    private int lastIndex(Path root) throws IOException {
        return Files.list(root)
                .filter(Files::isRegularFile)
                .filter(path -> this.pattern.matcher(path.getFileName().toString()).matches())
                .map(path -> {
                    try (final ReadableByteChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
                        return Header.read(ch);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Header::getIndex)
                .max(Comparator.comparingInt(left -> left))
                .orElse(0);
    }

    @Override
    public void log(Package pack) throws IOException {
        final Command[] commands = pack.getCommands();
        final ByteBuffer[] buffers = new ByteBuffer[1 + commands.length];
        buffers[0] = ByteBuffer.allocate(PACKAGE_SIGNATURE.length + 2 * Long.BYTES + Integer.BYTES);

        buffers[0].put(PACKAGE_SIGNATURE);

        final UUID transactionId = pack.getTransactionId();
        buffers[0].putLong(transactionId.getMostSignificantBits());
        buffers[0].putLong(transactionId.getLeastSignificantBits());
        buffers[0].putInt(commands.length);
        buffers[0].rewind();

        for (int i = 0, count = commands.length; i < count; i++) {
            buffers[i + 1] = this.serializer.serialize(commands[i]);
        }

        this.channel.write(buffers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<Package> get(long fromTimestamp) throws IOException {
        Path[] paths;
        final Pair<Long, Path>[] pairs = Files.list(this.root)
                .filter(Files::isRegularFile)
                .filter(path -> this.pattern.matcher(path.getFileName().toString()).matches())
                .map(path -> {
                    try (final ReadableByteChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
                        return Pair.of(Header.read(ch), path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparingInt(pair -> pair.getLeft().getIndex()))
                .map(pair -> Pair.of(pair.getLeft().getTimestamp(), pair.getRight()))
                .toArray(Pair[]::new);

        for (int i = 0; i < pairs.length; i++) {
            if (fromTimestamp <= pairs[i].getLeft()) {
                return Arrays.stream(pairs, i > 0 ? i - 1 : i, pairs.length)
                        .map(Pair::getRight)
                        .flatMap(path -> {
                            try {
                                final FileChannel ch = FileChannel.open(path, StandardOpenOption.READ);
                                final Header header = Header.read(ch);
                                return StreamSupport.stream(new PackageSpliterator(ch), false);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }

        return Stream.of();
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

    private class PackageSpliterator implements Spliterator<Package> {
        private static final Logger LOG = Logger.getLogger(PackageSpliterator.class.getName());

        private static final Cleaner CLEANER = Cleaner.create();

        private final ReadableByteChannel channel;

        private final Cleaner.Cleanable cleanable;

        public PackageSpliterator(ReadableByteChannel channel) {
            this.channel = channel;
            this.cleanable = CLEANER.register(this, () -> {
                try {
                    LOG.info("Close channel" + this.channel);
                    this.channel.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Error were occurred while channel closing!");
                }
            });
        }

        @Override
        public boolean tryAdvance(Consumer<? super Package> action) {
            final boolean result;
            try {
                final ByteBuffer packHeader = ByteBuffer.allocate(PACKAGE_SIGNATURE.length + 2 * Long.BYTES + Integer.BYTES);
                do {
                    if (this.channel.read(packHeader) < 0) {
                        if (packHeader.position() == 0) {
                            this.channel.close();
                            return false;
                        } else {
                            throw new EOFException();
                        }
                    }
                } while (packHeader.position() < packHeader.capacity());

                final byte[] signature = new byte[PACKAGE_SIGNATURE.length];

                packHeader.rewind();
                packHeader.get(signature);

                if (Arrays.equals(PACKAGE_SIGNATURE, signature)) {
                    final UUID transactionId = new UUID(packHeader.getLong(), packHeader.getLong());
                    final int commandCount = packHeader.getInt();
                    final Command[] commands = new Command[commandCount];

                    if (commandCount >= 0) {
                        for (int i = 0; i < commandCount; i++) {
                            commands[i] = FileLog.this.deserializer.deserialize(this.channel);
                        }

                        final Package pack = new Package(transactionId, commands);
                        action.accept(pack);
                    } else {
                        throw new IllegalStateException("Command count less then 0!");
                    }
                } else {
                    throw new IllegalStateException(String.format("Invalid package signature!"));
                }

                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Spliterator<Package> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return Spliterator.IMMUTABLE & Spliterator.NONNULL;
        }
    }
}
