package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.common.xa.Xid;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class XaWrapperCommandTest extends AbstractCommadTest {
    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final PutCommand wrappedCommand = new PutCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                this.valueTranslator.from(RandomStringUtils.random(10)),
                this.valueTranslator.from(RandomStringUtils.random(10))
        );
        final XaWrapperCommand<?> command = new XaWrapperCommand<>(sequenceNumber, xid, wrappedCommand);

        this.serializer.serialize(baos, command);

        final InputStream is = new ByteArrayInputStream(baos.toByteArray());
        final XaWrapperCommand<?> deserializedCommand = deserializer.deserialize(is);
        final PutCommand deserializedWrappedCommand = (PutCommand) deserializedCommand.getCommand();
        Assertions.assertNotNull(deserializedWrappedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedWrappedCommand.getSequenceNumber());
        Assertions.assertEquals(wrappedCommand.getSpaceName(), deserializedWrappedCommand.getSpaceName());
        Assertions.assertEquals(wrappedCommand.getKey(), deserializedWrappedCommand.getKey());
        Assertions.assertEquals(wrappedCommand.getValue(), deserializedWrappedCommand.getValue());
    }
}
