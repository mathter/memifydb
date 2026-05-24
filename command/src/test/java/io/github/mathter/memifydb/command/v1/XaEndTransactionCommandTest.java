package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.CommandDeserializer;
import io.github.mathter.memifydb.command.CommandSerializationFactory;
import io.github.mathter.memifydb.command.CommandSerializer;
import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.common.xa.Xid;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

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
public class XaEndTransactionCommandTest {
    final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);

    final CommandSerializer serializer = this.factory.serializer();

    final CommandDeserializer deserializer = this.factory.deserializer();

    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final XaEndTransactionCommand command = new XaEndTransactionCommand(sequenceNumber, xid, RandomUtils.nextInt());

        this.serializer.serialize(baos, command);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ReadableByteChannel channel = Channels.newChannel(bais);

        final XaEndTransactionCommand deserializedCommand = this.deserializer.deserialize(channel);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(xid, deserializedCommand.getXid());
    }

    @Test
    public void testChannelStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final WritableByteChannel channel = Channels.newChannel(baos);
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final XaEndTransactionCommand command = new XaEndTransactionCommand(sequenceNumber, xid, RandomUtils.nextInt());

        channel.write(this.serializer.serialize(command).rewind());

        final XaEndTransactionCommand deserializedCommand = this.deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(xid, deserializedCommand.getXid());
    }
}
