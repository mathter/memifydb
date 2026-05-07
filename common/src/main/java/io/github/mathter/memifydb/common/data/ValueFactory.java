package io.github.mathter.memifydb.common.data;

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
