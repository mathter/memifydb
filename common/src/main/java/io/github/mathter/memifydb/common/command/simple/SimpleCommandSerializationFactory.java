package io.github.mathter.memifydb.common.command.simple;

import io.github.mathter.memifydb.common.command.CommandDeserializer;
import io.github.mathter.memifydb.common.command.CommandSerializationFactory;
import io.github.mathter.memifydb.common.command.CommandSerializer;

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
