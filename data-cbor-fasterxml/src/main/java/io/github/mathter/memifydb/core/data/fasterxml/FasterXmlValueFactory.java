package io.github.mathter.memifydb.core.data.fasterxml;

import io.github.mathter.memifydb.core.data.ValueDeserializer;
import io.github.mathter.memifydb.core.data.ValueFactory;
import io.github.mathter.memifydb.core.data.ValueSerializer;
import io.github.mathter.memifydb.core.data.ValueTranslator;

public class FasterXmlValueFactory extends ValueFactory {
    public static final String ID = "fasterxml";

    private static final Mapper MAPPER = new Mapper();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ValueSerializer serializer() {
        return MAPPER;
    }

    @Override
    public ValueDeserializer deserializer() {
        return MAPPER;
    }

    @Override
    public ValueTranslator translator() {
        return MAPPER;
    }
}
