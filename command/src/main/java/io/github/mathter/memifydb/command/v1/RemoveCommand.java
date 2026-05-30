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
 * Remove command from space.
 */
public class RemoveCommand extends AbstractCommand {
    private static final byte[] PREFIX = {0x01, 0x0B};

    private final String spaceName;

    private final Value key;

    public RemoveCommand(SequenceNumber sequenceNumber, String spaceName, Value key) {
        super(sequenceNumber);
        this.spaceName = spaceName;
        this.key = key;
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

    @Override
    public int hashCode() {
        return Objects.hash(PREFIX, this.spaceName, this.key);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof RemoveCommand another
                && Objects.equals(this.spaceName, another.spaceName)
                && Objects.equals(this.key, another.key)
        );
    }
}
