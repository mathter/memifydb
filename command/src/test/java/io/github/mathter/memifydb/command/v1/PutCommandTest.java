package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.CommandDeserializer;
import io.github.mathter.memifydb.command.CommandSerializationFactory;
import io.github.mathter.memifydb.command.CommandSerializer;
import io.github.mathter.memifydb.command.SequenceNumber;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class PutCommandTest {
    final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);

    final CommandSerializer serializer = this.factory.serializer();

    final CommandDeserializer deserializer = this.factory.deserializer();

    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final PutCommand command = new PutCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                RandomUtils.nextBytes(10),
                RandomUtils.nextBytes(100)
        );

        this.serializer.serialize(baos, command);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ReadableByteChannel channel = Channels.newChannel(bais);

        final PutCommand deserializedCommand = this.deserializer.deserialize(channel);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
        Assertions.assertArrayEquals(command.getRawValue(), deserializedCommand.getRawValue());
    }

    @Test
    public void testChannelStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final WritableByteChannel channel = Channels.newChannel(baos);
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final PutCommand command = new PutCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                RandomUtils.nextBytes(10),
                RandomUtils.nextBytes(100)
        );

        channel.write(this.serializer.serialize(command));

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final PutCommand deserializedCommand = this.deserializer.deserialize(bais);

        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
        Assertions.assertArrayEquals(command.getRawValue(), deserializedCommand.getRawValue());
    }
}
