package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.space.simple.impl.SimpleSpaceFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

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
public class SimpleSpaceFactoryTest {
    @Test
    public void testCreate() {
        final String name = RandomStringUtils.random(10);

        final SpaceFactory spaceFactory = SpaceFactory.getInstance(SimpleSpaceFactory.ID);
        Assertions.assertNotNull(spaceFactory);

        final Space space = spaceFactory.get(name, null);
        Assertions.assertNotNull(space);
        Assertions.assertEquals(name, space.name());
    }

    @Test
    public void testCreateWithUUID() {
        final UUID id = UUID.randomUUID();
        final String name = RandomStringUtils.random(10);

        final SpaceFactory spaceFactory = SpaceFactory.getInstance(SimpleSpaceFactory.ID);
        Assertions.assertNotNull(spaceFactory);

        final Space space = spaceFactory.get(name, Map.of(Const.PROPERTY_ID, id));
        Assertions.assertNotNull(space);
        Assertions.assertEquals(id, space.id());
        Assertions.assertEquals(name, space.name());
    }

    @Test
    public void testCreateWithStringUUID() {
        final UUID id = UUID.randomUUID();
        final String name = RandomStringUtils.random(10);

        final SpaceFactory spaceFactory = SpaceFactory.getInstance(SimpleSpaceFactory.ID);
        Assertions.assertNotNull(spaceFactory);

        final Space space = spaceFactory.get(name, Map.of(Const.PROPERTY_ID, id.toString()));
        Assertions.assertNotNull(space);
        Assertions.assertEquals(id, space.id());
        Assertions.assertEquals(name, space.name());
    }
}
