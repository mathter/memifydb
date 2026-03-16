package io.github.mathter.memifydb.core.fasterxml;

import io.github.mathter.memifydb.core.value.ValueFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FasterXmlValueFactoryTest {
    @Test
    public void testLoad() {
        final ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);
        Assertions.assertNotNull(valueFactory);
    }
}
