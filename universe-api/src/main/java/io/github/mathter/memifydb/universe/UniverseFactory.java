package io.github.mathter.memifydb.universe;

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
public abstract class UniverseFactory {
    public static final UniverseFactory getInstance(String id) {
        final ServiceLoader<UniverseFactory> loader = ServiceLoader.load(UniverseFactory.class);

        for (UniverseFactory factory : loader) {
            if (id.equals(factory.id())) {
                return factory;
            }
        }

        throw new IllegalStateException(
                String.format("There is no %s with id='%s'", UniverseFactory.class, id)
        );
    }

    /**
     * Method returns realization if of this factory.
     *
     * @return id.
     */
    public abstract String id();

    public abstract Universe newInstance(Map<?, ?> properties);
}
