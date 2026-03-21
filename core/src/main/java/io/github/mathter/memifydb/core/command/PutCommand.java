package io.github.mathter.memifydb.core.command;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
}
