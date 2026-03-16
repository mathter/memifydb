package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

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
