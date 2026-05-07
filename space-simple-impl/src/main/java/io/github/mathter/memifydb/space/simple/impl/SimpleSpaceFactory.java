package io.github.mathter.memifydb.space.simple.impl;

import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.space.simple.Const;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
public class SimpleSpaceFactory extends SpaceFactory {
    public static final String ID = SimpleSpaceFactory.class.getName();

    @Override
    public String id() {
        return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Space> T get(String name, Map<?, ?> properties) {
        return (T) new SimpleSpace(
                this.buildId(properties),
                Objects.requireNonNull(name, "'name' parameter can't be null!"),
                this.buildTransactionTimeout(properties)
        );
    }

    private UUID buildId(Map<?, ?> properties) {
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

    private long buildTransactionTimeout(Map<?, ?> properties) {
        final long result;

        if (properties != null && properties.get(Const.PROPERTY_TRANSACTION_TIMEOUT) != null) {
            final Object transactionTimeoutObject = properties.get(Const.PROPERTY_TRANSACTION_TIMEOUT);

            if (transactionTimeoutObject instanceof String string) {
                result = Long.parseLong(string);
            } else if (transactionTimeoutObject instanceof Number number) {
                result = number.longValue();
            } else {
                throw new IllegalStateException(String.format("'%s' is not valid transaction timeout"));
            }
        } else {
            result = Const.DEFAULT_TRANSACTION_TIMEOUT * 1000;
        }

        return result;
    }
}
