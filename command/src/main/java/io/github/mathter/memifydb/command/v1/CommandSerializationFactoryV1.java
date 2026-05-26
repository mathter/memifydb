package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.CommandSerializationFactory;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.ValueSerializer;

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
class CommandSerializationFactoryV1 extends CommandSerializationFactory {
    public static final String ID = "simple";

    private final ValueFactory valueFactory;

    private final ValueSerializer valueSerializer;

    private final ValueDeserializer valueDeserializer;

    public CommandSerializationFactoryV1(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
        this.valueSerializer = valueFactory.serializer();
        this.valueDeserializer = valueFactory.deserializer();
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public io.github.mathter.memifydb.command.CommandSerializer serializer() {
        return new CommandSerializerV1(this.valueSerializer, this.valueDeserializer);
    }

    @Override
    public io.github.mathter.memifydb.command.CommandDeserializer deserializer() {
        return new CommandDeserializerV1(this.valueSerializer, this.valueDeserializer);
    }

    @Override
    public ValueFactory valueFactory() {
        return this.valueFactory;
    }
}
