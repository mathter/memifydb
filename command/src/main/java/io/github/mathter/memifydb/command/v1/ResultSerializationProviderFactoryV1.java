package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.ResultSerializationProvider;
import io.github.mathter.memifydb.command.spi.ResultSerializationProviderFactory;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;

import java.util.Map;

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
public class ResultSerializationProviderFactoryV1 implements ResultSerializationProviderFactory {
    public static final String PROPERTY_VALUE_FACTORY = "value-factory";

    @Override
    public String id() {
        return Const.ID;
    }

    @Override
    public ResultSerializationProvider provide(Map<?, ?> properties) {
        return new ResultSerializationProviderV1(buildValueFactory(properties));
    }

    private static ValueFactory buildValueFactory(Map<?, ?> properties) {
        final ValueFactory result;
        final Object idObject;

        if (properties != null && (idObject = properties.get(PROPERTY_VALUE_FACTORY)) != null) {
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
}
