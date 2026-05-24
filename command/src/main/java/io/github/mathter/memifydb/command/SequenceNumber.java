package io.github.mathter.memifydb.command;

import io.github.mathter.memifydb.common.util.ByteArray;

import java.io.Serial;

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
public class SequenceNumber extends Number {
    public static final int BYTES = 4;
    @Serial
    private static final long serialVersionUID = 1L;

    public static final long MAX_VALUE = 0xffffffffL;

    public static final long MIN_VALUE = 0x00000000L;

    private final long value;

    public SequenceNumber(int value) {
        this.value = 0xffffffffl & value;
    }

    public SequenceNumber(long value) {
        if (value > MAX_VALUE) {
            throw new IllegalArgumentException(
                    String.format("value is out of range [%s, %s]",
                            MIN_VALUE,
                            MAX_VALUE
                    )
            );
        }
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return (float) this.value;
    }

    @Override
    public double doubleValue() {
        return (double) this.value;
    }

    public SequenceNumber next() {
        final SequenceNumber result;
        final long next = this.value + 1;

        if (next >= MAX_VALUE) {
            return new SequenceNumber(MIN_VALUE);
        } else {
            result = new SequenceNumber(next);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SequenceNumber another && this.value == another.value;
    }

    @Override
    public int hashCode() {
        return (int) this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
