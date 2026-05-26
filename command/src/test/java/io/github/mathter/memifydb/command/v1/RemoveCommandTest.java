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

public class RemoveCommandTest extends AbstractCommadTest {
    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final RemoveCommand command = new RemoveCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                this.valueTranslator.from(RandomStringUtils.random(10))
        );

        this.serializer.serialize(baos, command);

        final InputStream is = new ByteArrayInputStream(baos.toByteArray());
        final RemoveCommand deserializedCommand = deserializer.deserialize(is);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertEquals(command.getKey(), deserializedCommand.getKey());
    }
}
