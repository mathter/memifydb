package io.github.mathter.memifydb.space;

import java.util.UUID;

/**
 * Interface of the space. Its contains data stored in a database. It is like as table in a RDBMS.
 */
public interface Space {
    /**
     * Unique identifier of the space.
     *
     * @return unique identifier.
     */
    public UUID id();

    /**
     * Name of the space. It is like as table name on a RDBMS.
     *
     * @return
     */
    public String name();
}
