package io.github.mathter.memifydb.core.net;

import io.github.mathter.memifydb.core.net.spi.NetworkFactoryProvider;

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
public abstract class NetworkFactory {
    public static NetworkFactory getInstance(String id) {
        final ServiceLoader<NetworkFactoryProvider> loader = ServiceLoader.load(NetworkFactoryProvider.class);

        for (NetworkFactoryProvider provider : loader) {
            if (id.equals(provider.id())) {
                return provider.provide();
            }
        }

        throw new IllegalArgumentException("No such network factory provider " + id);
    }

    public abstract Network newInstance(Map<?, ?> properties);
}
