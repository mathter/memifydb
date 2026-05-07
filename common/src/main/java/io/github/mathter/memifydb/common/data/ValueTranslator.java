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
 * Translator java objects to {@linkplain Value}.
 */
public interface ValueTranslator {
    /**
     * Method returns {@linkplain Value} representation of java specified object.
     *
     * @param object java object.
     * @param <T>    type of the java object.
     * @return {@linkplain Value} representation of the java object.
     */
    public <T> Value from(T object);

    /**
     * Method translates value to the specific java instance of type {@code clazz}
     *
     * @param value value.
     * @param clazz targert class.
     * @param <T>   type of class.
     * @return java instance.
     */
    public <T> T to(Value value, Class<T> clazz);
}
