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
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class RemoveCommandTest {
    final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);

    final CommandSerializer serializer = this.factory.serializer();

    final CommandDeserializer deserializer = this.factory.deserializer();

    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final RemoveCommand command = new RemoveCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                RandomUtils.nextBytes(10)
        );

        this.serializer.serialize(baos, command);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ReadableByteChannel channel = Channels.newChannel(bais);
        final RemoveCommand deserializedCommand = deserializer.deserialize(channel);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
    }

    @Test
    public void testChannelStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final WritableByteChannel channel = Channels.newChannel(baos);
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final RemoveCommand command = new RemoveCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                RandomUtils.nextBytes(10)
        );

        channel.write(this.serializer.serialize(command));

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final RemoveCommand deserializedCommand = this.deserializer.deserialize(bais);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
    }
}
