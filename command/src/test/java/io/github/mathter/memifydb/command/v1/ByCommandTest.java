package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByCommandTest extends AbstractCommadTest {
    @Test
    public void test() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final ByCommand command = new ByCommand(sequenceNumber);

        this.serializer.serialize(baos, command);

        final InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ByCommand deserializedCommand = this.deserializer.deserialize(bais);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
    }
}
