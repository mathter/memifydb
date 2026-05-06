package io.github.mathter.memifydb.common.data.fasterxml;

import io.github.mathter.memifydb.common.data.ValueFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FasterXmlValueFactoryTest {
    @Test
    public void testLoad() {
        final ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);
        Assertions.assertNotNull(valueFactory);
    }
}
