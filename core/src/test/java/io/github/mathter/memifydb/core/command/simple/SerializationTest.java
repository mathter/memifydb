package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.PutCommand;
import io.github.mathter.memifydb.core.command.RemoveCommand;
import io.github.mathter.memifydb.core.command.CommandSerializationFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SerializationTest {
    @Test
    public void testPutCommand() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(SimpleCommandSerializationFactory.ID);
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
    public void testPutCommandChannel() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(SimpleCommandSerializationFactory.ID);
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final Path path = Files.createTempFile(Path.of("./target"), "serialize", ".bin");
        final PutCommand command;
        try (final OutputStream os = Files.newOutputStream(path)) {
            command = new PutCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10), RandomUtils.nextBytes(100));

            serializer.serialize(os, command);
        }

        final PutCommand deserializedCommand;
        try (final FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            deserializedCommand = (PutCommand) deserializer.deserialize(ch);
        }

        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
        Assertions.assertArrayEquals(command.getRawValue(), deserializedCommand.getRawValue());
    }

    @Test
    public void testRemoveCommand() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get("simple");
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

    @Test
    public void testRemoveCommandChannel() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(SimpleCommandSerializationFactory.ID);
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final Path path = Files.createTempFile(Path.of("./target"), "serialize", ".bin");
        final RemoveCommand command;
        try (final OutputStream os = Files.newOutputStream(path)) {
            command = new RemoveCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10));

            serializer.serialize(os, command);
        }

        final RemoveCommand deserializedCommand;
        try (final FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            deserializedCommand = (RemoveCommand) deserializer.deserialize(ch);
        }

        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
    }
}
