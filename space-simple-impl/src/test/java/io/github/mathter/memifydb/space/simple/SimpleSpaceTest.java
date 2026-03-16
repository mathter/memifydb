package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.core.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.core.util.Opt;
import io.github.mathter.memifydb.core.value.Value;
import io.github.mathter.memifydb.core.value.ValueFactory;
import io.github.mathter.memifydb.space.KeyValueSpace;
import io.github.mathter.memifydb.space.SpaceFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleSpaceTest {
    private KeyValueSpace space;

    private ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);

    @BeforeEach
    public void init() {
        this.space = SpaceFactory.getInstance(SimpleSpaceFactory.ID).get(RandomStringUtils.random(10));
    }

    @Test
    public void test() {
        final Value key0 = this.valueFactory.translator().from(RandomStringUtils.random(10));
        final Value key1 = this.valueFactory.translator().from(RandomStringUtils.random(10));
        final Value value0 = this.valueFactory.translator().from(RandomStringUtils.random(10));
        final Value value1 = this.valueFactory.translator().from(RandomStringUtils.random(10));

        final Opt<Value> opt0 = this.space.put(key0, value0);
        Assertions.assertNotNull(opt0);
        Assertions.assertTrue(opt0.isEmpty());

        final Opt<Value> opt1 = this.space.put(key0, value1);
        Assertions.assertNotNull(opt1);
        Assertions.assertTrue(opt1.isPresent());
        Assertions.assertEquals(value0, opt1.get());

        final Opt<Value> opt2 = this.space.get(key1);
        Assertions.assertNotNull(opt2);
        Assertions.assertTrue(opt2.isEmpty());
    }

    @Test
    public void testNullKey() {
        Assertions.assertThrowsExactly(NullPointerException.class, () -> this.space.put(null, null));
    }
}
