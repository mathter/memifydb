package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.common.util.Opt;
import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.space.simple.impl.SimpleSpaceFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
public class SimpleSpaceTest {
    private KeyValueOperations operations;

    private ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);

    @BeforeEach
    public void init() {
        this.operations = (KeyValueOperations) SpaceFactory.getInstance(SimpleSpaceFactory.ID)
                .get(RandomStringUtils.random(10))
                .operatons();
    }

    @Test
    public void test() {
        final Value key0 = this.valueFactory.translator().from(RandomStringUtils.random(10));
        final Value key1 = this.valueFactory.translator().from(RandomStringUtils.random(10));
        final Value value0 = this.valueFactory.translator().from(RandomStringUtils.random(10));
        final Value value1 = this.valueFactory.translator().from(RandomStringUtils.random(10));

        final Opt<Value> opt0 = this.operations.put(key0, value0);
        Assertions.assertNotNull(opt0);
        Assertions.assertTrue(opt0.isEmpty());

        final Opt<Value> opt1 = this.operations.put(key0, value1);
        Assertions.assertNotNull(opt1);
        Assertions.assertTrue(opt1.isPresent());
        Assertions.assertEquals(value0, opt1.get());

        final Opt<Value> opt2 = this.operations.get(key1);
        Assertions.assertNotNull(opt2);
        Assertions.assertTrue(opt2.isEmpty());
    }

    @Test
    public void testNullKey() {
        Assertions.assertThrowsExactly(NullPointerException.class, () -> this.operations.put(null, null));
    }
}
