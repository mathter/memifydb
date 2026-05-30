package io.github.mathter.memifydb.core.net.spi.socket;

import io.github.mathter.memifydb.command.Command;
import io.github.mathter.memifydb.command.v1.ByCommand;
import io.github.mathter.memifydb.command.v1.SelectUniverseCommand;
import io.github.mathter.memifydb.command.v1.ValueResult;
import io.github.mathter.memifydb.universe.Universe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class Task implements Runnable {
    private static final Logger LOG = Logger.getLogger(Task.class.getName());

    private final SocketNetwork socketNetwork;

    private final Socket socket;

    private final InputStream is;

    private final OutputStream os;

    public Task(SocketNetwork socketNetwork, Socket socket) throws IOException {
        this.socketNetwork = socketNetwork;
        this.socket = socket;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                final Command command = this.socketNetwork.commandDeserializer.deserialize(this.is);

                if (command != null) {
                    this.processCommand(command);
                } else {
                    LOG.info(String.format("No command received for %s", this.socket.getRemoteSocketAddress()));
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, String.format("Socket error for %s!", this.socketNetwork), e);
        } finally {
            LOG.info(String.format("Closing %s", this.socket));
            this.socketNetwork.acceptSocketThread.connectionCount--;
        }
    }

    private void processCommand(Command command) throws IOException {
        switch (command) {
            case SelectUniverseCommand cmd -> process(cmd);
            case ByCommand cmd -> process(cmd);
            default -> throw new IOException("Unknown command: " + command);
        }
    }

    private void process(ByCommand command) throws IOException {
        LOG.info(String.format("The remote host %s said goodbye", this.socket.getRemoteSocketAddress()));
        this.socketNetwork.commandSerializer.serialize(os, command);
        os.flush();
        Thread.currentThread().interrupt();
    }

    private void process(SelectUniverseCommand command) {
        try {
            final Universe universe = this.socketNetwork.universes.get(command.getUniverseName());

            if (universe != null) {
                this.socketNetwork.universe = universe;
                LOG.info(String.format("Set universe %s for socket %s", universe, this.socket));
                this.socketNetwork.resultSerializer.serialize(
                        this.os,
                        new ValueResult(
                                command.getSequenceNumber(),
                                universe.getValueFactory().translator().from(command.getUniverseName())
                        )
                );
            } else {

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    String.format(
                            "Error while processing command %s",
                            this.socket.getRemoteSocketAddress()
                    ),
                    e
            );
        }
    }
}
