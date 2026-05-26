package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.SequenceNumber;

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
class AbstractResult implements Result {
    private final SequenceNumber sequenceNumber;

    protected AbstractResult(SequenceNumber sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public static byte[] getPrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SequenceNumber getSequenceNumber() {
        return this.sequenceNumber;
    }
}
