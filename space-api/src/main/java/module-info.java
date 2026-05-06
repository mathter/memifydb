import io.github.mathter.memifydb.space.SpaceFactory;

module io.github.mathter.memifydb.space.api {
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.transaction.api;
    requires java.transaction.xa;

    exports io.github.mathter.memifydb.space;

    uses SpaceFactory;
}