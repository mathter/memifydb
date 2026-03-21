module io.github.mathter.memifydb.space.simple {
    requires io.github.mathter.memifydb.core;
    requires io.github.mathter.memifydb.space;
    requires io.github.mathter.memifydb.core.data.fasterxml;

    provides io.github.mathter.memifydb.space.SpaceFactory with io.github.mathter.memifydb.space.simple.SimpleSpaceFactory;
}