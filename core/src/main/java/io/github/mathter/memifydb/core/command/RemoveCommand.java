package io.github.mathter.memifydb.core.command;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * Remove command from space.
 */
public class RemoveCommand implements Command {
    private static final byte PREFIX = 0x0B;

    private final byte[] rawSpaceName;

    private final byte[] rawKey;

    public RemoveCommand(String spaceName, byte[] rawKey) {
        this.rawSpaceName = spaceName.getBytes(StandardCharsets.UTF_8);
        this.rawKey = rawKey;
    }

    public RemoveCommand(byte[] rawSpaceName, byte[] rawKey) {
        this.rawSpaceName = rawSpaceName;
        this.rawKey = rawKey;
    }

    public static byte getPrefix() {
        return PREFIX;
    }

    public byte[] getRawSpaceName() {
        return rawSpaceName;
    }

    public String getSpaceName() {
        return new String(this.rawSpaceName, StandardCharsets.UTF_8);
    }

    public byte[] getRawKey() {
        return rawKey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(PREFIX, this.rawSpaceName, this.rawKey);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof RemoveCommand another
                && Arrays.equals(this.rawSpaceName, another.rawSpaceName)
                && Arrays.equals(this.rawKey, another.rawKey)
        );
    }
}
