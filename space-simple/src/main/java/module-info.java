import io.github.mathter.memifydb.space.SpaceFactory;
import io.github.mathter.memifydb.space.simple.impl.SimpleSpaceFactory;

module io.github.mathter.memifydb.space.simple {
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.space.api;
    requires io.github.mathter.memifydb.core.data.fasterxml;

    requires io.github.mathter.memifydb.transaction.api;
    requires java.transaction.xa;
    requires java.logging;

    exports io.github.mathter.memifydb.space.simple;

    provides SpaceFactory with SimpleSpaceFactory;
}