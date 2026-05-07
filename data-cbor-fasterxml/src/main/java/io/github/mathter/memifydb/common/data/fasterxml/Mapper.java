package io.github.mathter.memifydb.common.data.fasterxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.data.ValueTranslator;

import java.io.IOException;

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
        try {
            return this.deserialize(this.mapper.writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(object + " can't be represented as Value!", e);
        }
    }

    @Override
    public <T> T to(Value value, Class<T> clazz) {
        if (value instanceof RawValue rawValue) {
            try {
                return this.mapper.readValue(rawValue.getRaw(), clazz);
            } catch (IOException e) {
                throw new IllegalStateException("Can't parse!", e);
            }
        } else {
            throw new IllegalStateException(value + " is not instance of valid type!");
        }
    }

    @Override
    public Value deserialize(byte[] bytes) {
        return new RawValue(this.mapper, bytes);
    }

    @Override
    public byte[] serialize(Value value) {
        if (value instanceof RawValue rawValue) {
            return rawValue.getRaw();
        } else {
            throw new IllegalStateException(value + " is not instance of valid type!");
        }
    }
}
