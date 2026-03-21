package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.RemoveCommand;
import io.github.mathter.memifydb.core.command.SerializationFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializationTest {
    @Test
    public void testPutCommand() throws IOException {
        final SerializationFactory factory = SerializationFactory.get("simple");
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PutCommand command = new PutCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10), RandomUtils.nextBytes(100));

        serializer.serialize(baos, command);

        final PutCommand deserializedCommand = (PutCommand) deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
        Assertions.assertArrayEquals(command.getRawValue(), deserializedCommand.getRawValue());
    }

    @Test
    public void testRemoveCommand() throws IOException {
        final SerializationFactory factory = SerializationFactory.get("simple");
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final RemoveCommand command = new RemoveCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10));

        serializer.serialize(baos, command);

        final RemoveCommand deserializedCommand = (RemoveCommand) deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
    }
}
