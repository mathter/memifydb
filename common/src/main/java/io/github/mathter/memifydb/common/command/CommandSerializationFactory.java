package io.github.mathter.memifydb.common.command;

import java.util.ArrayList;
import java.util.List;
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
