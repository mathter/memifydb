import io.github.mathter.memifydb.command.spi.CommandSerializationProviderFactory;
import io.github.mathter.memifydb.command.spi.ResultSerializationProviderFactory;
import io.github.mathter.memifydb.command.v1.CommandSerializationRoviderFactoryV1;
import io.github.mathter.memifydb.command.v1.ResultSerializationProviderFactoryV1;

module io.github.mathter.memifydb.command {
    requires java.transaction.xa;
    requires org.apache.commons.lang3;
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.core.data.fasterxml;

    exports io.github.mathter.memifydb.command;
    exports io.github.mathter.memifydb.command.v1;
    exports io.github.mathter.memifydb.command.xa;

    uses CommandSerializationProviderFactory;
    uses ResultSerializationProviderFactory;
    provides CommandSerializationProviderFactory with CommandSerializationRoviderFactoryV1;
    provides ResultSerializationProviderFactory with ResultSerializationProviderFactoryV1;
}