package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.space.Space;
import io.github.mathter.memifydb.space.SpaceFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SimpleSpaceFactory extends SpaceFactory {
    public static final String ID = SimpleSpaceFactory.class.getName();

    @Override
    public String id() {
        return ID;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Space> T get(String name, Map<?, ?> properties) {
        return (T) new SimpleSpace(this.buildId(properties), Objects.requireNonNull(name, "'name' parameter can't be null!"));
    }

    private UUID buildId(Map<?, ?> properties) {
        final UUID result;

        if (properties != null && properties.get(Const.PROPERTY_ID) != null) {
            final Object idObject = properties.get(Const.PROPERTY_ID);

            if (idObject instanceof String string) {
                result = UUID.fromString(string);
            } else if (idObject instanceof UUID uuid) {
                result = uuid;
            } else {
                throw new IllegalStateException(String.format("'%s' is not valid uuid~"));
            }
        } else {
            result = UUID.randomUUID();
        }

        return result;
    }
}
