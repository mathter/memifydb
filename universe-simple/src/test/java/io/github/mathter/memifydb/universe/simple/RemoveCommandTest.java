package io.github.mathter.memifydb.universe.simple;

import io.github.mathter.memifydb.command.Result;
import io.github.mathter.memifydb.command.SequenceNumber;
import io.github.mathter.memifydb.command.v1.PutCommand;
import io.github.mathter.memifydb.command.v1.RemoveCommand;
import io.github.mathter.memifydb.command.v1.ValueResult;
import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.space.simple.Const;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.UniverseFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
public class RemoveCommandTest {
    private static final String SPACE_NAME = RandomStringUtils.randomAlphabetic(10);

    private static final ValueFactory VALUE_FACTORY = ValueFactory.get(FasterXmlValueFactory.ID);

    private Universe universe;

    @BeforeEach
    public void setUp() {
        final SpaceFactory spaceFactory = SpaceFactory.getInstance(Const.ID);
        final Space space = spaceFactory.get(SPACE_NAME);

        this.universe = UniverseFactory.getInstance(io.github.mathter.memifydb.universe.simple.Const.ID)
                .newInstance(
                        Map.of(
                                io.github.mathter.memifydb.universe.simple.Const.PROPERTY_NAME, RandomStringUtils.randomAlphabetic(10),
                                io.github.mathter.memifydb.universe.simple.Const.PROPERTY_SPACES, List.of(space),
                                io.github.mathter.memifydb.universe.simple.Const.PROPERTY_VALUE_FACTORY, VALUE_FACTORY
                        )
                );
    }

    @Test
    public void test() throws Exception {
        final SequenceNumber sequenceNumber = new SequenceNumber(RandomUtils.nextInt());
        final String key = RandomStringUtils.randomAlphabetic(10);
        final String value = RandomStringUtils.randomAlphabetic(10);
        final PutCommand putCommand = new PutCommand(
                sequenceNumber,
                SPACE_NAME,
                VALUE_FACTORY.translator().from(key),
                VALUE_FACTORY.translator().from(value)
        );
        final RemoveCommand command = new RemoveCommand(
                sequenceNumber,
                SPACE_NAME,
                VALUE_FACTORY.translator().from(key)
        );

        Result processed = this.universe.process(command);
        Assertions.assertNotNull(processed);
        Assertions.assertNotNull(processed instanceof ValueResult);
        Assertions.assertNull(((ValueResult) processed).getValue());

        this.universe.process(putCommand);
        processed = this.universe.process(command);
        Assertions.assertNotNull(processed);
        Assertions.assertNotNull(processed instanceof ValueResult);
        Assertions.assertNotNull(((ValueResult) processed).getValue());

        final Value val = ((ValueResult) processed).getValue();
        Assertions.assertNotNull(val);
        Assertions.assertEquals(value, val.get());
    }
}
