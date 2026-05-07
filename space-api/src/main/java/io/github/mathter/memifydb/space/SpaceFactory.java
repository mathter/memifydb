package io.github.mathter.memifydb.space;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
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
