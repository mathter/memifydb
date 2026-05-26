package io.github.mathter.memifydb.common.data.fasterxml;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.util.Opt;
import tools.jackson.databind.ObjectMapper;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Objects;

public class JavaValue implements Value, Raw {
    private final ObjectMapper mapper;

    private final Object value;

    private final Class<?> clazz;

    private Reference<Opt<byte[]>> reference;

    public JavaValue(ObjectMapper mapper, Object value) {
        this.mapper = mapper;
        this.value = Objects.requireNonNull(value);
        this.clazz = value.getClass();
    }

    public JavaValue(ObjectMapper mapper, Class<?> clazz) {
        this.mapper = mapper;
        this.value = null;
        this.clazz = clazz;
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return (T) value;
    }

    @Override
    public byte[] getRaw() {
        final byte[] result;
        Opt<byte[]> tmp;

        if (this.reference == null || (tmp = this.reference.get()) == null) {
            result = this.mapper.writeValueAsBytes(this.value);
            this.reference = new SoftReference<>(Opt.of(result));
        } else {
            result = tmp.get();
        }

        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getRaw());
    }

    @Override
    public boolean equals(Object obj) {
        final boolean result;

        if (this == obj) {
            result = true;
        } else if (obj instanceof Value another) {
            result = Arrays.equals(this.getRaw(), another.getRaw());
        } else {
            result = false;
        }

        return result;
    }
}
