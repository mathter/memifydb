package io.github.mathter.memifydb.common.data;

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
 * This class is universal representation of any data in raw format.
 */
public interface Value {
    public byte[] getRaw();

    @SuppressWarnings("unchecked")
    public default <T> T get() {
        return (T) this.get(Object.class);
    }

    /**
     * The method returns the underlying data in a specific Java format.
     * The return value is determined by the format of the underlying data and there is no automatic conversion to type {@code T}.
     *
     * @param <T> type of java.
     * @return object.
     */
    public <T> T get(Class<T> clazz);
}
