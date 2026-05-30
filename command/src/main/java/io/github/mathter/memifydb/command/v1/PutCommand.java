package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.common.data.Value;

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

    private final String spaceName;

    private final Value key;

    private final Value value;

    public PutCommand(SequenceNumber sequenceNumber, String spaceName, Value key, Value value) {
        super(sequenceNumber);
        this.spaceName = spaceName;
        this.key = key;
        this.value = value;
    }

    public static byte[] getPrefix() {
        return PREFIX;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public Value getKey() {
        return this.key;
    }

    public Value getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(PREFIX, this.spaceName, this.key, this.value);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof PutCommand another
                && Objects.equals(this.spaceName, another.spaceName)
                && Objects.equals(this.key, another.key)
                && Objects.equals(this.value, another.value)
        );
    }
}
