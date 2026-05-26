import io.github.mathter.memifydb.command.spi.CommandSerializationFactoryProvider;
import io.github.mathter.memifydb.command.spi.ResultSerializationFactoryProvider;
import io.github.mathter.memifydb.command.v1.CommandSerializationFactoryProviderV1;
import io.github.mathter.memifydb.command.v1.ResultSerializationFactoryProviderV1;

module io.github.mathter.memifydb.command {
    requires java.transaction.xa;
    requires org.apache.commons.lang3;
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.core.data.fasterxml;

    exports io.github.mathter.memifydb.command;
    exports io.github.mathter.memifydb.command.v1;
    exports io.github.mathter.memifydb.command.xa;

    uses io.github.mathter.memifydb.command.spi.CommandSerializationFactoryProvider;
    uses io.github.mathter.memifydb.command.spi.ResultSerializationFactoryProvider;
    provides CommandSerializationFactoryProvider with CommandSerializationFactoryProviderV1;
    provides ResultSerializationFactoryProvider with ResultSerializationFactoryProviderV1;
}