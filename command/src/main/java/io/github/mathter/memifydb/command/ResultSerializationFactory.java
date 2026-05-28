package io.github.mathter.memifydb.command;

import io.github.mathter.memifydb.command.spi.ResultSerializationFactoryProvider;
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
public abstract class ResultSerializationFactory {
    public static ResultSerializationFactory get(String id) {
        return get(id, null);
    }

    public static ResultSerializationFactory get(String id, Map<?, ?> properties) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }

        final ServiceLoader<ResultSerializationFactoryProvider> loader = ServiceLoader.load(ResultSerializationFactoryProvider.class);

        for (ResultSerializationFactoryProvider provider : loader) {
            if (id.equals(provider.id())) {
                return provider.provide(properties);
            }
        }

        throw new IllegalArgumentException("No such result serialization factory with id " + id);
    }

    public abstract ResultSerializer serializer();

    public abstract ResultDeserializer deserializer();

    public abstract ValueFactory valueFactory();
}
