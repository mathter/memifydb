package io.github.mathter.memifydb.log.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializationFactory;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.log.Log;
import io.github.mathter.memifydb.log.Package;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * File header:
 * | memifydbwal | fileindex (int 4 bytes) | creation timestamp (long 8 bytes) |
 */
class FileLog implements Log {
    private static final byte[] PACKAGE_SIGNATURE = new byte[]{0x0B, 0x01};

    private static final String FILE_PATTERN = "%s%s%s";

    private final Pattern pattern;

    private final CommandSerializer serializer;

    private final CommandDeserializer deserializer;

    private final GatheringByteChannel channel;


    public FileLog(CommandSerializationFactory commandSerializationFactory,
                   Path root,
                   String fileNamePrefix,
                   String fileNamePostfix,
                   int fileMaxSize) {
        this.serializer = commandSerializationFactory.serializer();
        this.deserializer = commandSerializationFactory.deserializer();
        this.pattern = Pattern.compile(
                String.format("^(%s)\\d+(%s)$", fileNamePrefix, fileNamePostfix)
        );

        try {
            this.channel = new Channel(root, fileNamePrefix, fileNamePostfix, fileMaxSize, lastIndex(root));
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
        final List<Command> commands = pack.getCommands();
        final ByteBuffer[] buffers = new ByteBuffer[1 + commands.size()];
        buffers[0] = ByteBuffer.allocate(PACKAGE_SIGNATURE.length + 2 * Long.BYTES);

        buffers[0].put(PACKAGE_SIGNATURE);

        final UUID transactionId = pack.getTransactionId();
        buffers[0].putLong(transactionId.getMostSignificantBits());
        buffers[0].putLong(transactionId.getLeastSignificantBits());
        buffers[0].rewind();

        for (int i = 0, count = commands.size(); i < count; i++) {
            buffers[i + 1] = this.serializer.serialize(commands.get(i));
        }

        this.channel.write(buffers);
    }

    @Override
    public Stream<Package> get(long fromTimestamp) throws IOException {
        throw new UnsupportedOperationException();
    }
}
