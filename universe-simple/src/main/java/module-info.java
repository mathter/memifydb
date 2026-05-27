import io.github.mathter.memifydb.universe.UniverseFactory;
import io.github.mathter.memifydb.universe.simple.impl.SImpleUniverseFactory;

module universe.simple {
    requires org.apache.commons.lang3;
    requires io.github.mathter.memifydb.universe.api;
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.command;
    requires io.github.mathter.memifydb.transaction.api;
    requires io.github.mathter.memifydb.space.api;
    requires io.github.mathter.memifydb.space.simple;
    requires io.github.mathter.memifydb.core.data.fasterxml;
    requires java.logging;

    exports io.github.mathter.memifydb.universe.simple;
    exports io.github.mathter.memifydb.universe.simple.impl;

    provides UniverseFactory with SImpleUniverseFactory;
}