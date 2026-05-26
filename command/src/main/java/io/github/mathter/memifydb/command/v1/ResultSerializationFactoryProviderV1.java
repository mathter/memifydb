package io.github.mathter.memifydb.command.v1;

import io.github.mathter.memifydb.command.ResultSerializationFactory;
import io.github.mathter.memifydb.command.spi.ResultSerializationFactoryProvider;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;

import java.util.Map;

public class ResultSerializationFactoryProviderV1 implements ResultSerializationFactoryProvider {
    public static final String ID = "simple";

    public static final String PROPERTY_VALUE_FACTORY = "value-factory";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public ResultSerializationFactory get(Map<?, ?> properties) {
        return new ResultSerializationFactoryV1(buildValueFactory(properties));
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
