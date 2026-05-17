import io.github.mathter.memifydb.common.command.v1.CommandSerializationFactoryV1;

module io.github.mathter.memifydb.common {
    requires java.transaction.xa;
    exports io.github.mathter.memifydb.common.xa;
    exports io.github.mathter.memifydb.common.util;
    exports io.github.mathter.memifydb.common.data;
    exports io.github.mathter.memifydb.common.command;
    exports io.github.mathter.memifydb.common.command.xa;
    exports io.github.mathter.memifydb.common.command.v1;
    exports io.github.mathter.memifydb.common.util.nio;

    uses io.github.mathter.memifydb.common.data.ValueFactory;
    uses io.github.mathter.memifydb.common.command.CommandSerializationFactory;

    provides io.github.mathter.memifydb.common.command.CommandSerializationFactory with CommandSerializationFactoryV1;
}