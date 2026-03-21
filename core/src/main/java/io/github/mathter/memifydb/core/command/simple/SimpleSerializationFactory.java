package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.SerializationFactory;

public class SimpleSerializationFactory extends SerializationFactory {
    public static final String ID = "simple";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public CommandSerializer serializer() {
        return new Serializer();
    }

    @Override
    public CommandDeserializer deserializer() {
        return new Deserializer();
    }
}
