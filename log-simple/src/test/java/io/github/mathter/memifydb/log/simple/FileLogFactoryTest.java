package io.github.mathter.memifydb.log.simple;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.command.v1.CommandSerializationFactoryProviderV1;
import io.github.mathter.memifydb.command.v1.PutCommand;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
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
public class FileLogFactoryTest {
    private final ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);

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
                                .map(e -> new PutCommand(
                                                new SequenceNumber(0),
                                                spaceName,
                                                this.valueFactory.translator().from(e.getLeft()),
                                                this.valueFactory.translator().from(e.getLeft())
                                        )
                                )
                                .toArray(Command[]::new)
                ),
                new Package(
                        UUID.randomUUID(),
                        keyValue1.stream()
                                .map(e -> new PutCommand(
                                                new SequenceNumber(0),
                                                spaceName,
                                                this.valueFactory.translator().from(e.getLeft()),
                                                this.valueFactory.translator().from(e.getLeft())
                                        )
                                )
                                .toArray(Command[]::new)
                )
        };
        final Log log = factory.get(
                Map.of(
                        FileLogFactory.Const.PARAM_LOG_ROOT_DIR, root,
                        FileLogFactory.Const.PARAM_FILE_MAX_SIZE, 100,
                        FileLogFactory.Const.PARAM_COMMAND_SERELIZATION_FACTORY,
                        CommandSerializationFactoryProviderV1.ID
                )
        );

        log.log(packages[0]);
        log.log(packages[1]);

        final Package[] readed = log.get(0).toArray(Package[]::new);
        Assertions.assertArrayEquals(packages, readed);
    }
}
