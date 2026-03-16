package io.github.mathter.memifydb.space;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * Space factory.
 */
public abstract class SpaceFactory {
    /**
     * Method returns the factory realization identified by parameter {@code id}.
     *
     * @param id factory realization id.
     * @return factory.
     */
    public static final SpaceFactory getInstance(String id) {
        final ServiceLoader<SpaceFactory> serviceLoader = ServiceLoader.load(SpaceFactory.class);

        for (SpaceFactory factory : serviceLoader) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException(
                String.format("There is no %s with id='%s'", SpaceFactory.class, id)
        );
    }

    /**
     * Method returns realization if of this factory.
     *
     * @return id.
     */
    public abstract String id();

    /**
     * Method return new instance of the space with default properties.
     *
     * @param name name of the space.
     * @param <T>  type of the space.
     * @return space.
     */
    public <T extends Space> T get(String name) {
        return this.get(name, null);
    }

    /**
     * Method return new instance of the space with specified properties.
     *
     * @param name name of the space.
     * @param <T>  type of the space.
     * @return space.
     */
    public abstract <T extends Space> T get(String name, Map<?, ?> properties);
}
