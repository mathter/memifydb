package io.github.mathter.memifydb.command;

import io.github.mathter.memifydb.command.spi.CommandSerializationFactoryProvider;
import io.github.mathter.memifydb.common.data.ValueFactory;

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
 */
public abstract class CommandSerializationFactory {
    public static final CommandSerializationFactory get(String id) {
        return get(id, null);
    }

    public static final CommandSerializationFactory get(String id, Map<?, ?> properties) {
        final ServiceLoader<CommandSerializationFactoryProvider> serviceLoader = ServiceLoader.load(CommandSerializationFactoryProvider.class);

        for (CommandSerializationFactoryProvider provider : serviceLoader) {
            if (id.equals(provider.id())) {
                return provider.provide(properties);
            }
        }

        throw new IllegalStateException(
                String.format("There is no %s with id='%s'", CommandSerializationFactory.class, id)
        );
    }

    public abstract String id();

    public abstract CommandSerializer serializer();

    public abstract CommandDeserializer deserializer();

    public abstract ValueFactory valueFactory();
}
