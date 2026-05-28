package io.github.mathter.memifydb.core.net.socket;

import io.github.mathter.memifydb.core.net.Network;
import io.github.mathter.memifydb.core.net.NetworkFactory;
import io.github.mathter.memifydb.universe.Universe;
import io.github.mathter.memifydb.universe.UniverseFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.List;
import java.util.Map;

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
public class SocketNetworkCreationTest {
    @Test
    public void test() throws Exception {
        final NetworkFactory factory = NetworkFactory.getInstance(Const.ID);
        final Universe universe = UniverseFactory.getInstance(io.github.mathter.memifydb.universe.simple.Const.ID)
                .newInstance(
                        Map.of(
                                io.github.mathter.memifydb.universe.simple.Const.PROPERTY_NAME, RandomStringUtils.randomAlphabetic(10)
                        )
                );

        try (final Network network = factory.newInstance(
                Map.of(
                        Const.PROPERTY_ADDRESS, "localhost",
                        Const.PROPERTY_PORT, 1234,
                        Const.PROPERTY_BACKLOG, 10,
                        Const.PROPERTY_MAXCONNECTIONCOUNT, 10,
                        Const.PROPERTY_UNIVERSES, List.of(universe)
                )
        )) {
            network.start();

            final Socket socket = new Socket("localhost", 1234);

            socket.getInputStream().read();
        }
    }
}
