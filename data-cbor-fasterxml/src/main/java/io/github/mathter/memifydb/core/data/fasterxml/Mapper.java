package io.github.mathter.memifydb.core.data.fasterxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import io.github.mathter.memifydb.core.data.ValueTranslator;
import io.github.mathter.memifydb.core.data.Value;
import io.github.mathter.memifydb.core.data.ValueDeserializer;
import io.github.mathter.memifydb.core.data.ValueSerializer;

import java.io.IOException;

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
