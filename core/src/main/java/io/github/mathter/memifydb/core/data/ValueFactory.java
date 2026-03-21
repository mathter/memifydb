package io.github.mathter.memifydb.core.data;

import java.util.ServiceLoader;

/**
 * The class returns the instances of the serilizer, deseralizer, and translator of {@code Value} type objects
 * to and from a byte array, as well as java objects
 */
public abstract class ValueFactory {
    /**
     * Method returns the factory realization identified by parameter {@code id}.
     *
     * @param id factory realization id.
     * @return factory.
     */
    public static ValueFactory get(String id) {
        final ServiceLoader<ValueFactory> serviceLoader = ServiceLoader.load(ValueFactory.class);

        for (ValueFactory factory : serviceLoader) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException(
                String.format("There is no %s with id='%s'", ValueFactory.class, id)
        );
    }

    /**
     * Method returns realization if of this factory.
     *
     * @return id.
     */
    public abstract String id();

    /**
     * Method returns serializer of data.
     *
     * @return serializer.
     */
    public abstract ValueSerializer serializer();

    /**
     * Method returns deserializer of data.
     *
     * @return deserializer.
     */
    public abstract ValueDeserializer deserializer();

    /**
     * Method returns translator of data.
     *
     * @return translator.
     */
    public abstract ValueTranslator translator();
}
