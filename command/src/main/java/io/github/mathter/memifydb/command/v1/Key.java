package io.github.mathter.memifydb.command.v1;

import java.util.Arrays;

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
class Key {
    final byte[] prefix;

    public Key(byte[] prefix) {
        this.prefix = prefix;
    }


    @Override
    public boolean equals(Object obj) {
        boolean result;

        if (obj instanceof Key another) {
            result = this.prefix[0] == another.prefix[0] && this.prefix[1] == another.prefix[1];
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return this.prefix[0] ^ this.prefix[1];
    }

    @Override
    public String toString() {
        return Arrays.toString(this.prefix);
    }
}
