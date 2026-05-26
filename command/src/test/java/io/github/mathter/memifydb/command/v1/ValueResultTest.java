package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ValueResultTest extends AbstractResultTest {
    @Test
    public void testStreamChannel() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final String value = RandomStringUtils.randomAlphanumeric(10);
        final byte[] rawValue = valueFactory.serializer().serialize(valueFactory.translator().from(value));
        final ValueResult result = new ValueResult(
                sequenceNumber,
                rawValue
        );

        this.serializer.serialize(baos, result);

        final InputStream is = new ByteArrayInputStream(baos.toByteArray());
        final ValueResult deserializedResult = this.deserializer.deserialize(is);
        Assertions.assertNotNull(deserializedResult);
        Assertions.assertEquals(sequenceNumber, deserializedResult.getSequenceNumber());
        Assertions.assertArrayEquals(rawValue, deserializedResult.getRawValue());
    }
}
