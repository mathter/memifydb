module io.github.mathter.memifydb.core {
    exports io.github.mathter.memifydb.core.util;
    exports io.github.mathter.memifydb.core.data;
    exports io.github.mathter.memifydb.core.command;

    uses io.github.mathter.memifydb.core.data.ValueFactory;
    uses io.github.mathter.memifydb.core.command.SerializationFactory;

    provides io.github.mathter.memifydb.core.command.SerializationFactory with io.github.mathter.memifydb.core.command.simple.SimpleSerializationFactory;
}