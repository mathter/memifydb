import io.github.mathter.memifydb.core.net.spi.NetworkFactoryProvider;
import io.github.mathter.memifydb.core.net.spi.socket.SocketNetworkFactoryProvider;

module io.github.mathter.memifydb.core {
    requires java.logging;
    requires org.apache.commons.lang3;
    requires io.github.mathter.memifydb.universe.api;
    requires io.github.mathter.memifydb.universe.simple;
    requires io.github.mathter.memifydb.command;
    requires io.github.mathter.memifydb.common;
    requires io.github.mathter.memifydb.core.data.fasterxml;
    requires io.github.mathter.memifydb.space.simple;

    exports io.github.mathter.memifydb.core.net;
    exports io.github.mathter.memifydb.core.net.socket;

    uses NetworkFactoryProvider;

    provides NetworkFactoryProvider with SocketNetworkFactoryProvider;
}