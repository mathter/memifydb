package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.SequenceNumber;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VoidResultTest extends AbstractResultTest {
    @Test
    public void test() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final Result result = new VoidResult(sequenceNumber);

        this.serializer.serialize(baos, result);
        final InputStream is = new ByteArrayInputStream(baos.toByteArray());
        final Result deserializedResult = this.deserializer.deserialize(is);
        Assertions.assertNotNull(deserializedResult);
        Assertions.assertEquals(sequenceNumber, deserializedResult.getSequenceNumber());
    }
}
