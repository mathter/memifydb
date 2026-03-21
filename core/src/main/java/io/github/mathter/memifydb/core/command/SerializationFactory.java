package io.github.mathter.memifydb.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public abstract class SerializationFactory {
    public static final List<String> factories() {
        final List<String> result = new ArrayList<>();
        final ServiceLoader<SerializationFactory> serviceLoader = ServiceLoader.load(SerializationFactory.class);

        for (SerializationFactory factory : serviceLoader) {
            result.add(factory.id());
        }

        return result;
    }

    public static final SerializationFactory get(String id) {
        final ServiceLoader<SerializationFactory> serviceLoader = ServiceLoader.load(SerializationFactory.class);

        for (SerializationFactory factory : serviceLoader) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException(
                String.format("There is no %s with id='%s'", SerializationFactory.class, id)
        );
    }

    public abstract String id();

    public abstract CommandSerializer serializer();

    public abstract CommandDeserializer deserializer();
}
