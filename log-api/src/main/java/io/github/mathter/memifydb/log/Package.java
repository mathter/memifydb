package io.github.mathter.memifydb.log;

import io.github.mathter.memifydb.common.command.Command;

import java.util.Objects;
import java.util.UUID;

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
 */
public class Package {
    private final UUID transactionId;

    private final Command[] commands;

    public Package(UUID transactionId, Command command) {
        this(transactionId, new Command[]{command});
    }

    public Package(UUID transactionId, Command[] commands) {
        this.transactionId = Objects.requireNonNull(transactionId);
        this.commands = commands;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Command[] getCommands() {
        return commands;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
                || (obj instanceof Package another
                && Objects.equals(this.transactionId, another.transactionId)
                && Objects.deepEquals(this.commands, another.commands));
    }

    @Override
    public int hashCode() {
        return this.transactionId.hashCode();
    }
}
