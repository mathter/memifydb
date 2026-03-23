package io.github.mathter.memifydb.log.simple;

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

        final LogFactory factory = LogFactory.get(FileLogFactory.ID);
        Assertions.assertNotNull(factory);

        final Path root = Files.createTempDirectory(Paths.get("./target"), "tmp-log");
        final Log log = factory.get(
                Map.of(
                        Const.LOG_ROOT_DIR, root,
                        Const.FILE_MAX_SIZE, 100,
                        Const.COMMAND_SERELIZATION_FACTORY,
                        SimpleCommandSerializationFactory.ID
                )
        );

        log.log(
                new Package(
                        UUID.randomUUID(),
                        keyValue0.stream()
                                .map(e -> new PutCommand(spaceName, e.getLeft(), e.getLeft()))
                                .toList()
                )
        );

        log.log(
                new Package(
                        UUID.randomUUID(),
                        keyValue1.stream()
                                .map(e -> new PutCommand(spaceName, e.getLeft(), e.getLeft()))
                                .toList()
                )
        );
    }
}
