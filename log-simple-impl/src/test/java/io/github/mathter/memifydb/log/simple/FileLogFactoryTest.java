package io.github.mathter.memifydb.log.simple;

import io.github.mathter.memifydb.core.command.Command;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.simple.SimpleCommandSerializationFactory;
import io.github.mathter.memifydb.log.Log;
import io.github.mathter.memifydb.log.LogFactory;
import io.github.mathter.memifydb.log.Package;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class FileLogFactoryTest {
    @Test
    public void test() throws IOException {
        final String spaceName = RandomStringUtils.random(10);
        final List<Pair<byte[], byte[]>> keyValue0 = IntStream.range(0, 10)
                .mapToObj(e -> Pair.of(RandomUtils.nextBytes(10), RandomUtils.nextBytes(100)))
                .toList();

        final List<Pair<byte[], byte[]>> keyValue1 = IntStream.range(0, 10)
                .mapToObj(e -> Pair.of(RandomUtils.nextBytes(10), RandomUtils.nextBytes(100)))
                .toList();

        final LogFactory factory = LogFactory.get(FileLogFactory.Const.FACTORY_ID);
        Assertions.assertNotNull(factory);

        final Path root = Files.createTempDirectory(Paths.get("./target"), "tmp-log");
        final Package[] packages = {
                new Package(
                        UUID.randomUUID(),
                        keyValue0.stream()
                                .map(e -> new PutCommand(spaceName, e.getLeft(), e.getLeft()))
                                .toArray(Command[]::new)
                ),
                new Package(
                        UUID.randomUUID(),
                        keyValue1.stream()
                                .map(e -> new PutCommand(spaceName, e.getLeft(), e.getLeft()))
                                .toArray(Command[]::new)
                )
        };
        final Log log = factory.get(
                Map.of(
                        FileLogFactory.Const.PARAM_LOG_ROOT_DIR, root,
                        FileLogFactory.Const.PARAM_FILE_MAX_SIZE, 100,
                        FileLogFactory.Const.PARAM_COMMAND_SERELIZATION_FACTORY,
                        SimpleCommandSerializationFactory.ID
                )
        );

        log.log(packages[0]);
        log.log(packages[1]);

        final Package[] readed = log.get(0).toArray(Package[]::new);
        Assertions.assertArrayEquals(packages, readed);
    }
}
