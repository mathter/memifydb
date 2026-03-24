package io.github.mathter.memifydb.core.command;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Command to put data in key-value space.
 */
public class PutCommand implements Command {
    private static final byte PREFIX = 0x0A;

    private final byte[] rawSpaceName;

    private final byte[] rawKey;

    private final byte[] rawValue;

    public PutCommand(String spaceName, byte[] rawKey, byte[] rawValue) {
        this.rawSpaceName = spaceName.getBytes(StandardCharsets.UTF_8);
        this.rawKey = rawKey;
        this.rawValue = rawValue;
    }

    public PutCommand(byte[] rawSpaceName, byte[] rawKey, byte[] rawValue) {
        this.rawSpaceName = rawSpaceName;
        this.rawKey = rawKey;
        this.rawValue = rawValue;
    }

    public static byte getPrefix() {
        return PREFIX;
    }

    public byte[] getRawSpaceName() {
        return this.rawSpaceName;
    }

    public String getSpaceName() {
        return new String(this.rawSpaceName, StandardCharsets.UTF_8);
    }

    public byte[] getRawKey() {
        return this.rawKey;
    }

    public byte[] getRawValue() {
        return this.rawValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(PREFIX, this.rawSpaceName, this.rawKey, this.rawValue);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof PutCommand another
                && Arrays.equals(this.rawSpaceName, another.rawSpaceName)
                && Arrays.equals(this.rawKey, another.rawKey)
                && Arrays.equals(this.rawValue, another.rawValue)
        );
    }
}
