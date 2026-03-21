package io.github.mathter.memifydb.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class CommandSerializationFactory {
    public static final List<String> factories() {
        final List<String> result = new ArrayList<>();
        final ServiceLoader<CommandSerializationFactory> serviceLoader = ServiceLoader.load(CommandSerializationFactory.class);

        for (CommandSerializationFactory factory : serviceLoader) {
            result.add(factory.id());
        }

        return result;
    }

    public static final CommandSerializationFactory get(String id) {
        final ServiceLoader<CommandSerializationFactory> serviceLoader = ServiceLoader.load(CommandSerializationFactory.class);

        for (CommandSerializationFactory factory : serviceLoader) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException(
                String.format("There is no %s with id='%s'", CommandSerializationFactory.class, id)
        );
    }

    public abstract String id();

    public abstract CommandSerializer serializer();

    public abstract CommandDeserializer deserializer();
}
