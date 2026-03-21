package io.github.mathter.memifydb.log;

import java.util.Map;
import java.util.ServiceLoader;

public abstract class LogFactory {
    public static final LogFactory get(String id) {
        final ServiceLoader<LogFactory> serviceLoader = ServiceLoader.load(LogFactory.class);

        for (LogFactory factory : serviceLoader) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException(
                String.format("There is no LogFactory with id='%s", id)
        );
    }

    public abstract String id();

    public abstract Log get(Map<?, ?> properties);
}
