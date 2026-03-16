package io.github.mathter.memifydb.core.fasterxml;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mathter.memifydb.core.value.Value;
import io.github.mathter.memifydb.core.util.Opt;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

class RawValue implements Value {
    private final ObjectMapper mapper;

    private final byte[] raw;

    private Reference<Opt<?>> reference;

    public RawValue(ObjectMapper mapper, byte[] raw) {
        this.mapper = mapper;
        this.raw = raw;
        this.reference = null;
    }

    public byte[] getRaw() {
        return raw;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get() {
        final T result;
        Opt<T> tmp;

        if (this.reference == null || (tmp = (Opt<T>) this.reference.get()) == null) {
            result = this.calc();
            this.reference = new SoftReference<>(Opt.of(result));
        } else {
            result = tmp.get();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T calc() {
        final T result;

        try {
            result = (T) this.mapper.readValue(this.raw, Object.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can't parse data!", e);
        }

        return result;
    }
}
