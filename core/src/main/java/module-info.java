import io.github.mathter.memifydb.core.net.spi.NetworkFactoryProvider;
import io.github.mathter.memifydb.core.net.spi.socket.SocketNetworkFactoryProvider;

module io.github.mathter.memifydb.core {
    requires java.logging;
    requires org.apache.commons.lang3;
    requires io.github.mathter.memifydb.universe.api;
    requires io.github.mathter.memifydb.universe.simple;

    exports io.github.mathter.memifydb.core.net;
    exports io.github.mathter.memifydb.core.net.socket;

    uses NetworkFactoryProvider;

    provides NetworkFactoryProvider with SocketNetworkFactoryProvider;
}