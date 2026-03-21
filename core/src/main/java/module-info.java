module io.github.mathter.memifydb.core {
    exports io.github.mathter.memifydb.core.util;
    exports io.github.mathter.memifydb.core.data;
    exports io.github.mathter.memifydb.core.command;
    exports io.github.mathter.memifydb.core.command.simple;

    uses io.github.mathter.memifydb.core.data.ValueFactory;
    uses io.github.mathter.memifydb.core.command.CommandSerializationFactory;

    provides io.github.mathter.memifydb.core.command.CommandSerializationFactory with io.github.mathter.memifydb.core.command.simple.SimpleCommandSerializationFactory;
}