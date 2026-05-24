package io.github.mathter.memifydb.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SequenceNumberTest {
    @Test
    public void testFromLong() {
        SequenceNumber sequenceNumber = new SequenceNumber(SequenceNumber.MIN_VALUE);
        Assertions.assertEquals(SequenceNumber.MIN_VALUE, sequenceNumber.longValue());

        sequenceNumber = new SequenceNumber(SequenceNumber.MAX_VALUE);
        Assertions.assertEquals(SequenceNumber.MAX_VALUE, sequenceNumber.longValue());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SequenceNumber(SequenceNumber.MAX_VALUE + 1)
        );
    }

    @Test
    public void testFromInt() {
        SequenceNumber sequenceNumber = new SequenceNumber((int) SequenceNumber.MIN_VALUE);
        Assertions.assertEquals((int) SequenceNumber.MIN_VALUE, sequenceNumber.intValue());

        sequenceNumber = new SequenceNumber((int) SequenceNumber.MAX_VALUE);
        Assertions.assertEquals((int) SequenceNumber.MAX_VALUE, sequenceNumber.intValue());
    }
}
