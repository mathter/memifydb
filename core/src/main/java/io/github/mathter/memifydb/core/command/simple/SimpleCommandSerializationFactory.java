package io.github.mathter.memifydb.core.command.simple;

import io.github.mathter.memifydb.core.command.CommandDeserializer;
import io.github.mathter.memifydb.core.command.CommandSerializer;
import io.github.mathter.memifydb.core.command.CommandSerializationFactory;

public class SimpleCommandSerializationFactory extends CommandSerializationFactory {
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
