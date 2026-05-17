package io.github.mathter.memifydb.common.command.v1;

import io.github.mathter.memifydb.common.command.CommandDeserializer;
import io.github.mathter.memifydb.common.command.CommandSerializationFactory;
import io.github.mathter.memifydb.common.command.CommandSerializer;
import io.github.mathter.memifydb.common.util.nio.InputStreamChannel;
import io.github.mathter.memifydb.common.util.nio.OutputStreamChannel;
import io.github.mathter.memifydb.common.xa.Xid;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

public class XaStartTransactionCommandTest {
    final CommandSerializationFactory factory = CommandSerializationFactory.get(CommandSerializationFactoryV1.ID);

    final CommandSerializer serializer = this.factory.serializer();

    final CommandDeserializer deserializer = this.factory.deserializer();

    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final XaStartTransactionCommand command = new XaStartTransactionCommand(xid);

        this.serializer.serialize(baos, command);

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ReadableByteChannel channel = new InputStreamChannel(bais);

        final XaStartTransactionCommand deserializedCommand = this.deserializer.deserialize(channel);
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(xid, deserializedCommand.getXid());
    }

    @Test
    public void testChannelStream() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamChannel channel = new OutputStreamChannel(baos);
        final Xid xid = Xid.of(0, RandomUtils.nextBytes(10), RandomUtils.nextBytes(10));
        final XaStartTransactionCommand command = new XaStartTransactionCommand(xid);

        channel.write(this.serializer.serialize(command).rewind());

        final XaStartTransactionCommand deserializedCommand = this.deserializer.deserialize(new ByteArrayInputStream(baos.toByteArray()));
        Assertions.assertNotNull(deserializedCommand);
        Assertions.assertEquals(xid, deserializedCommand.getXid());
    }
}
