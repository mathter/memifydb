package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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
public class InitCommandTest extends AbstractCommadTest {
    @Test
    public void test() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final InitCommand.Info info = new InitCommand.Info();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());

        info.setClientId(UUID.randomUUID());
        final InitCommand command = new InitCommand(sequenceNumber, info);

        this.serializer.serialize(baos, command);

        final InputStream is = new ByteArrayInputStream(baos.toByteArray());
        final InitCommand deserializedCommand = this.deserializer.deserialize(is);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(info.getClientId(), deserializedCommand.getArgument().getClientId());
    }
}
