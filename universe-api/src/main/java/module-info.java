module io.github.mathter.memifydb.universe.api {
    exports io.github.mathter.memifydb.universe;
    requires io.github.mathter.memifydb.space.api;
    requires io.github.mathter.memifydb.command;
    requires java.transaction.xa;
    requires io.github.mathter.memifydb.common;

    uses io.github.mathter.memifydb.universe.UniverseFactory;
}