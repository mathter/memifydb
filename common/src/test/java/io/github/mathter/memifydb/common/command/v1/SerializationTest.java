package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.CommandDeserializer;
import io.github.mathter.memifydb.common.command.CommandSerializationFactory;
import io.github.mathter.memifydb.common.command.CommandSerializer;
import io.github.mathter.memifydb.common.util.nio.InputStreamChannel;
import io.github.mathter.memifydb.common.util.nio.OutputStreamChannel;
import io.github.mathter.memifydb.common.xa.Xid;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
public class SerializationTest {
    @Test
    public void testXaWrapped() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final PutCommand wrappedCommand = new PutCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10), RandomUtils.nextBytes(100));
        final XaWrapper<?> command = new XaWrapper<>(xid, wrappedCommand);

        serializer.serialize(baos, command);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ReadableByteChannel channel = new InputStreamChannel(bais);

        final XaWrapper<?> deserializedCommand = deserializer.deserialize(channel);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(xid, deserializedCommand.getXid());

        final PutCommand deserializedWrappedCommand = (PutCommand) deserializedCommand.getCommand();
        Assertions.assertNotNull(deserializedWrappedCommand);
        Assertions.assertEquals(wrappedCommand.getSpaceName(), deserializedWrappedCommand.getSpaceName());
        Assertions.assertArrayEquals(wrappedCommand.getRawKey(), deserializedWrappedCommand.getRawKey());
        Assertions.assertArrayEquals(wrappedCommand.getRawValue(), deserializedWrappedCommand.getRawValue());
    }

    @Test
    public void testXaWrappedChannel() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamChannel channel = new OutputStreamChannel(baos);
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final PutCommand wrappedCommand = new PutCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10), RandomUtils.nextBytes(100));
        final XaWrapper<?> command = new XaWrapper<>(xid, wrappedCommand);

        channel.write(serializer.serialize(command).rewind());

        final XaWrapper<?> deserializedCommand = deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(xid, deserializedCommand.getXid());

        final PutCommand deserializedWrappedCommand = (PutCommand) deserializedCommand.getCommand();
        Assertions.assertNotNull(deserializedWrappedCommand);
        Assertions.assertEquals(wrappedCommand.getSpaceName(), deserializedWrappedCommand.getSpaceName());
        Assertions.assertArrayEquals(wrappedCommand.getRawKey(), deserializedWrappedCommand.getRawKey());
        Assertions.assertArrayEquals(wrappedCommand.getRawValue(), deserializedWrappedCommand.getRawValue());
    }

    @Test
    public void testPutCommand() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);
        final CommandSerializer serializer = factory.serializer();
        final CommandDeserializer deserializer = factory.deserializer();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PutCommand command = new PutCommand(RandomStringUtils.random(10), RandomUtils.nextBytes(10), RandomUtils.nextBytes(100));

        serializer.serialize(baos, command);

        final PutCommand deserializedCommand = deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
        Assertions.assertArrayEquals(command.getRawValue(), deserializedCommand.getRawValue());
    }

    @Test
    public void testPutCommandChannel() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);
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
            deserializedCommand = deserializer.deserialize(ch);
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

        final RemoveCommand deserializedCommand = deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
    }

    @Test
    public void testRemoveCommandChannel() throws IOException {
        final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);
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
            deserializedCommand = deserializer.deserialize(ch);
        }

        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(command.getSpaceName(), deserializedCommand.getSpaceName());
        Assertions.assertArrayEquals(command.getRawKey(), deserializedCommand.getRawKey());
    }
}
