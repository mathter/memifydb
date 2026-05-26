package io.github.mathter.memifydb.common.data.fasterxml;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.util.Opt;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
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
 */
class RawValue implements Value, Raw {
    private final ObjectMapper mapper;

    private final byte[] raw;

    private Reference<Opt<Object>> reference;

    public RawValue(ObjectMapper mapper, byte[] raw) {
        this.mapper = mapper;
        this.raw = raw;
        this.reference = null;
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        final T result;
        Opt<Object> tmp = null;

        if (this.reference == null || (tmp = this.reference.get()) == null) {
            result = this.calc(clazz);
            this.reference = new SoftReference<>(Opt.of(result));
        } else {
            result = (T) tmp.get();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T calc(Class<T> clazz) {
        final T result;

        try {
            result = (T) this.mapper.readValue(this.raw, clazz);
        } catch (JacksonException e) {
            throw new IllegalStateException("Can't parse data!", e);
        }

        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.raw);
    }

    @Override
    public boolean equals(Object obj) {
        final boolean result;

        if (this == obj) {
            result = true;
        } else if (obj instanceof RawValue another) {
            return Arrays.equals(this.raw, another.raw);
        } else if (obj instanceof Value value) {
            result = Arrays.equals(this.raw, value.getRaw());
        } else {
            result = false;
        }

        return result;
    }
}
