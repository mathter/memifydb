package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.CommandDeserializer;
import io.github.mathter.memifydb.command.CommandSerializationProvider;
import io.github.mathter.memifydb.command.CommandSerializer;
import io.github.mathter.memifydb.common.data.ValueDeserializer;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.ValueSerializer;
import io.github.mathter.memifydb.common.data.ValueTranslator;

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
public class AbstractCommadTest {
    protected final CommandSerializationProvider factory = CommandSerializationProvider.get(Const.ID);

    protected final CommandSerializer serializer = this.factory.serializer();

    protected final CommandDeserializer deserializer = this.factory.deserializer();

    protected final ValueFactory valueFactory = this.factory.valueFactory();

    protected final ValueSerializer valueSerializer = this.valueFactory.serializer();

    protected final ValueDeserializer valueDeserializer = this.valueFactory.deserializer();

    protected final ValueTranslator valueTranslator = this.valueFactory.translator();
}
