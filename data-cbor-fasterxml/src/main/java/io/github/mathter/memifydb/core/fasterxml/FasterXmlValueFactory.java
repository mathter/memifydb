package io.github.mathter.memifydb.core.fasterxml;

import io.github.mathter.memifydb.core.value.ValueTranslator;
import io.github.mathter.memifydb.core.value.ValueDeserializer;
import io.github.mathter.memifydb.core.value.ValueFactory;
import io.github.mathter.memifydb.core.value.ValueSerializer;

public class FasterXmlValueFactory extends ValueFactory {
    public static final String ID = FasterXmlValueFactory.class.getName();

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
