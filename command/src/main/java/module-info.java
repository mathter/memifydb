import io.github.mathter.memifydb.command.v1.CommandSerializationFactoryV1;

module io.github.mathter.memifydb.command {
    requires java.transaction.xa;
    requires io.github.mathter.memifydb.common;
    requires org.apache.commons.lang3;

    exports io.github.mathter.memifydb.command;
    exports io.github.mathter.memifydb.command.v1;
    exports io.github.mathter.memifydb.command.xa;

    uses io.github.mathter.memifydb.command.CommandSerializationFactory;
    provides io.github.mathter.memifydb.command.CommandSerializationFactory with CommandSerializationFactoryV1;
}