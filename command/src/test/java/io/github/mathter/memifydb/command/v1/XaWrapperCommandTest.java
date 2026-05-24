package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.CommandDeserializer;
import io.github.mathter.memifydb.command.CommandSerializationFactory;
import io.github.mathter.memifydb.command.CommandSerializer;
import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.common.xa.Xid;
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

public class XaWrapperCommandTest {
    final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);

    final CommandSerializer serializer = this.factory.serializer();

    final CommandDeserializer deserializer = this.factory.deserializer();

    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final PutCommand wrappedCommand = new PutCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                RandomUtils.nextBytes(10),
                RandomUtils.nextBytes(100)
        );
        final XaWrapperCommand<?> command = new XaWrapperCommand<>(sequenceNumber, xid, wrappedCommand);

        this.serializer.serialize(baos, command);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ReadableByteChannel channel = Channels.newChannel(bais);
        final XaWrapperCommand<?> deserializedCommand = deserializer.deserialize(channel);
        final PutCommand deserializedWrappedCommand = (PutCommand) deserializedCommand.getCommand();
        Assertions.assertNotNull(deserializedWrappedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedWrappedCommand.getSequenceNumber());
        Assertions.assertEquals(wrappedCommand.getSpaceName(), deserializedWrappedCommand.getSpaceName());
        Assertions.assertArrayEquals(wrappedCommand.getRawKey(), deserializedWrappedCommand.getRawKey());
        Assertions.assertArrayEquals(wrappedCommand.getRawValue(), deserializedWrappedCommand.getRawValue());
    }

    @Test
    public void testChannelStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final WritableByteChannel channel = Channels.newChannel(baos);
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final PutCommand wrappedCommand = new PutCommand(
                sequenceNumber,
                RandomStringUtils.random(10),
                RandomUtils.nextBytes(10),
                RandomUtils.nextBytes(100)
        );
        final XaWrapperCommand<?> command = new XaWrapperCommand<>(sequenceNumber, xid, wrappedCommand);

        channel.write(serializer.serialize(command));

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final XaWrapperCommand<?> deserializedCommand = this.deserializer.deserialize(bais);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedCommand.getSequenceNumber());
        Assertions.assertEquals(xid, deserializedCommand.getXid());

        final PutCommand deserializedWrappedCommand = (PutCommand) deserializedCommand.getCommand();
        Assertions.assertNotNull(deserializedWrappedCommand);
        Assertions.assertEquals(sequenceNumber, deserializedWrappedCommand.getSequenceNumber());
        Assertions.assertEquals(wrappedCommand.getSpaceName(), deserializedWrappedCommand.getSpaceName());
        Assertions.assertArrayEquals(wrappedCommand.getRawKey(), deserializedWrappedCommand.getRawKey());
        Assertions.assertArrayEquals(wrappedCommand.getRawValue(), deserializedWrappedCommand.getRawValue());
    }
}
