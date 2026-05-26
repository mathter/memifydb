package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
public class PutCommandTest extends AbstractCommadTest {
    @Test
    public void test() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final PutCommand command = new PutCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                this.valueTranslator.from(RandomStringUtils.random(10)),
                this.valueTranslator.from(RandomStringUtils.random(10))
        );

        this.serializer.serialize(baos, command);

        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());

        final PutCommand deserializedCommand = this.deserializer.deserialize(bais);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertEquals(command.getKey(), deserializedCommand.getKey());
        Assertions.assertEquals(command.getValue(), deserializedCommand.getValue());
    }
}
