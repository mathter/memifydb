package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;

public class VoidResult extends AbstractResult {
    private static final byte[] PREFIX = {0x01, 0x01};

    public VoidResult(SequenceNumber sequenceNumber) {
        super(sequenceNumber);
    }

    public static byte[] getPrefix() {
        return PREFIX;
    }
}
