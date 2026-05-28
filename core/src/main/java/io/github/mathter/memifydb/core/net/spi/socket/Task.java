package io.github.mathter.memifydb.core.net.spi.socket;

import java.io.IOException;
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

    public Task(SocketNetwork socketNetwork, Socket socket) {
        this.socketNetwork = socketNetwork;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.socket.close();
            throw new UnsupportedOperationException("Not supported yet.");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, String.format("Socket error for %s!", this.socketNetwork), e);
        } finally {
            LOG.info(String.format("Closing %s", this.socket));
            this.socketNetwork.acceptSocketThread.connectionCount--;
        }
    }
}
