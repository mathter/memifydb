module io.github.mathter.memifydb.common {
    exports io.github.mathter.memifydb.common.util;
    exports io.github.mathter.memifydb.common.data;
    exports io.github.mathter.memifydb.common.command;
    exports io.github.mathter.memifydb.common.command.simple;

    uses io.github.mathter.memifydb.common.data.ValueFactory;
    uses io.github.mathter.memifydb.common.command.CommandSerializationFactory;

    provides io.github.mathter.memifydb.common.command.CommandSerializationFactory with io.github.mathter.memifydb.common.command.simple.SimpleCommandSerializationFactory;
}