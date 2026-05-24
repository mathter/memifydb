package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.SequenceNumber;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

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
 * <p>
 * Command to put data in key-value space.
 */
public class PutCommand extends AbstractCommand {
    private static final byte[] PREFIX = {0x01, 0x0A};

    private final byte[] rawSpaceName;

    private final byte[] rawKey;

    private final byte[] rawValue;

    public PutCommand(SequenceNumber sequenceNumber, String spaceName, byte[] rawKey, byte[] rawValue) {
        super(sequenceNumber);
        this.rawSpaceName = spaceName.getBytes(StandardCharsets.UTF_8);
        this.rawKey = rawKey;
        this.rawValue = rawValue;
    }

    public PutCommand(SequenceNumber sequenceNumber, byte[] rawSpaceName, byte[] rawKey, byte[] rawValue) {
        super(sequenceNumber);
        this.rawSpaceName = rawSpaceName;
        this.rawKey = rawKey;
        this.rawValue = rawValue;
    }

    public static byte[] getPrefix() {
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
