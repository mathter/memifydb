package io.github.mathter.memifydb.common.data.fasterxml;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.data.ValueTranslator;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.cbor.CBORMapper;

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
class Mapper implements ValueSerializer, ValueDeserializer, ValueTranslator {
    private final ObjectMapper mapper = new CBORMapper();

    @Override
    public <T> Value from(T object) {
        final Value result;

        try {
            if (object == null) {
                result = new JavaValue(this.mapper, Object.class);
            } else {
                result = new JavaValue(this.mapper, object);
            }
        } catch (JacksonException e) {
            throw new IllegalStateException(object + " can't be represented as Value!", e);
        }

        return result;
    }

    @Override
    public <T> T to(Value value, Class<T> clazz) {
        final T result;

        if (value instanceof RawValue rawValue) {
            result = rawValue.get(clazz);
        } else if (value instanceof JavaValue javaValue) {
            result = javaValue.get(clazz);
        } else {
            throw new IllegalStateException(value + " is not instance of valid type!");
        }

        return result;
    }

    @Override
    public Value deserialize(byte[] bytes) {
        return new RawValue(this.mapper, bytes);
    }

    @Override
    public byte[] serialize(Object value) {
        final byte[] result;

        if (value instanceof Raw raw) {
            result = raw.getRaw();
        } else if (value instanceof Value val) {
            result = this.mapper.writeValueAsBytes(val.get());
        } else {
            result = this.mapper.writeValueAsBytes(value);
        }

        return result;
    }
}