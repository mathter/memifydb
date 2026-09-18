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
 *
 */
package io.github.mathter.memifydb.network.socket

import io.github.mathter.memifydb.network.NetworkFactory
import org.junit.jupiter.api.{Assertions, Test}

import java.net.{InetAddress, Socket}

class SocketNetworkFactoryTest {
  @Test
  def test(): Unit = {
    val socketAcceptor = new SocketHandler {
      override def handle(socket: Socket): Handle = new Handle {
        override def release(): Unit = {
        }
      }
    }

    val factory = NetworkFactory(Const.id)
    Assertions.assertNotNull(factory)

    val network = factory.instance("testNetwork", Map(
      Const.propertyAddress -> "localhost",
      Const.propertyPort -> 9999,
      Const.propertySocketHandler -> socketAcceptor
    ))
    Assertions.assertNotNull(network)

    network.start()
    Thread.sleep(1000)
    val socket = new Socket(InetAddress.getByName("localhost"), 9999)
    Assertions.assertTrue(socket.isConnected)

    socket.getOutputStream.write(0)
    socket.close()

    network.close()
  }
}
