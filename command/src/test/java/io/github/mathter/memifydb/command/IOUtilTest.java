package io.github.mathter.memifydb.command;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class IOUtilTest {
    @Test
    public void testStreamChannelSequenceNumber() throws IOException {
        final SequenceNumber origin = new SequenceNumber(RandomUtils.nextInt());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        IOUtil.write(os, origin);

        final ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(os.toByteArray()));
        final SequenceNumber deserialized = IOUtil.readSequenceNumber(channel);

        Assertions.assertEquals(origin, deserialized);
    }

    @Test
    public void testChannelStreamSequenceNumber() throws IOException {
        final SequenceNumber origin = new SequenceNumber(RandomUtils.nextInt());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buf = IOUtil.write(origin);

        os.write(buf);

        final ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        final SequenceNumber deserialized = IOUtil.readSequenceNumber(is);

        Assertions.assertEquals(origin, deserialized);
    }
}
