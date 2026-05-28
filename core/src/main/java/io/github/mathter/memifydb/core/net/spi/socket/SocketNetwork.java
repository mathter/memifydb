package io.github.mathter.memifydb.core.net.spi.socket;

import io.github.mathter.memifydb.core.net.Network;
import io.github.mathter.memifydb.universe.Universe;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SocketNetwork implements Network {
    private static final Logger LOG = Logger.getLogger(SocketNetwork.class.getName());

    private static final Cleaner CLEANER = Cleaner.create();

    private ServerSocket ss;

    private final InetAddress address;

    private final int port;

    private final int backlog;

    final int maxConnectionCount;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    final AcceptSocketThread acceptSocketThread = new AcceptSocketThread();

    final Map<String, Universe> universes;

    public SocketNetwork(
            InetAddress address,
            int port,
            int backlog,
            int maxConnectionCount,
            Collection<Universe> universes
    ) {
        this.address = address;
        this.port = port;
        this.backlog = backlog;
        this.maxConnectionCount = maxConnectionCount;
        this.universes = Optional.ofNullable(universes)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toMap(e -> e.getName(), Function.identity()));

        CLEANER.register(this, () -> {
            try {
                this.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, String.format("Socket network %s closed", this), e);
            }
        });
    }

    @Override
    public void start() throws IOException {
        LOG.info(String.format("Starting socket %s", this));

        if (this.ss != null) {
            throw new IllegalStateException("Socket already started!");
        }

        this.ss = new ServerSocket(this.port, this.backlog, this.address);
        this.acceptSocketThread.start();
    }

    @Override
    public void close() throws Exception {
        LOG.info(String.format("Stoping socket %s", this));

        this.acceptSocketThread.interrupt();
        this.executor.shutdown();
    }

    @Override
    public String toString() {
        return String.format(
                "network[address=%s, port=%s, backlog=%s, maxConnectionCount=%s]",
                SocketNetwork.this.address,
                SocketNetwork.this.port,
                SocketNetwork.this.backlog,
                SocketNetwork.this.maxConnectionCount
        );
    }

    public class AcceptSocketThread extends Thread {
        public int connectionCount = 0;

        @Override
        public void run() {
            LOG.info(String.format("Starting listening for %s", SocketNetwork.this));

            while (!Thread.interrupted()) {
                try {
                    final Socket socket = SocketNetwork.this.ss.accept();
                    LOG.info(String.format("Accepted socket connection from %s for %s", socket.getRemoteSocketAddress(), SocketNetwork.this));

                    if (this.connectionCount >= SocketNetwork.this.maxConnectionCount) {
                        LOG.info(String.format("Max connection count reached for %s!", SocketNetwork.this));
                        // TODO send information to the client about the maximum number of connections reached
                        socket.close();
                    }

                    SocketNetwork.this.executor.execute(new Task(SocketNetwork.this, socket));
                    this.connectionCount++;
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error accepting socket!", e);
                }
            }
        }
    }
}
