package io.github.mathter.memifydb.universe.simple.impl;

import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.UniverseFactory;
import io.github.mathter.memifydb.universe.simple.Const;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class SImpleUniverseFactory extends UniverseFactory {
    @Override
    public String id() {
        return Const.ID;
    }

    @Override
    public Universe newInstance(String id, Map<?, ?> properties) {
        return new SimpleUniverse(
                buildId(properties),
                buildValueFactory(properties),
                buildSpaces(properties)
        );
    }

    private static UUID buildId(Map<?, ?> properties) {
        final UUID result;

        if (properties != null && properties.get(Const.PROPERTY_ID) != null) {
            final Object idObject = properties.get(Const.PROPERTY_ID);

            if (idObject instanceof String string) {
                result = UUID.fromString(string);
            } else if (idObject instanceof UUID uuid) {
                result = uuid;
            } else {
                throw new IllegalStateException(String.format("'%s' is not valid uuid"));
            }
        } else {
            result = UUID.randomUUID();
        }

        return result;
    }

    private static ValueFactory buildValueFactory(Map<?, ?> properties) {
        final ValueFactory result;
        final Object idObject;

        if (properties != null && (idObject = properties.get(Const.PROPERTY_VALUE_FACTORY)) != null) {
            if (idObject instanceof String string) {
                result = ValueFactory.get(string);
            } else if (idObject instanceof ValueFactory valueFactory) {
                result = valueFactory;
            } else {
                throw new IllegalStateException(String.format("'%s' is not valid valueFactory"));
            }
        } else {
            result = ValueFactory.get(FasterXmlValueFactory.ID);
        }

        return result;
    }

    private static Collection<Space<?>> buildSpaces(Map<?, ?> properties) {
        final Collection<Space<?>> result;
        final Object idObject;

        if (properties != null && (idObject = properties.get(Const.PROPERTY_SPACES)) != null) {
            if (idObject instanceof Collection<?> collection) {
                result = collection.stream()
                        .map(e -> {
                            if (e instanceof Space) {
                                return (Space<?>) e;
                            } else {
                                throw new IllegalStateException(String.format("'%s' is not valid space"));
                            }
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
            } else {
                throw new IllegalStateException(String.format("'%s' is not valid space"));
            }
        } else {
            throw new IllegalStateException(String.format("'%s' is not valid spaces"));
        }

        return result;
    }
}
