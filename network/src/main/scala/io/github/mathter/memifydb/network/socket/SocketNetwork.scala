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

import io.github.mathter.memifydb.network.Network
import io.github.mathter.memifydb.network.socket.SocketNetwork.{cleaner, log}
import org.slf4j.LoggerFactory

import java.lang.ref.Cleaner
import java.net.{InetAddress, ServerSocket}
import java.util.logging.{Level, Logger}
import javax.net.ServerSocketFactory
import scala.collection.mutable
import scala.compiletime.uninitialized

class SocketNetwork(
                     val serverSocketFactory: ServerSocketFactory,
                     val address: InetAddress,
                     val port: Int,
                     val backlog: Int,
                     val maxConnectionCount: Int,
                     val socketHandler: SocketHandler
                   ) extends Network {
  private val thread = new Thread(new AcceptorLoop)

  private val handlers: mutable.Set[Handler] = mutable.Set.empty

  private var serverSocket: ServerSocket = uninitialized

  {
    cleaner.register(this, () => {
      SocketNetwork.log.info(s"Stop network ${this} connection serve")

      try {
        this.close()
      } catch {
        case e: Exception =>
          SocketNetwork.log.error(s"Error while network ${this} closing!")
      }
    })
  }

  override def start(): Unit = {
    if (this.serverSocket != null) {
      throw new IllegalStateException(s"Socket ${this} already started!")
    }
    this.thread.start()
  }

  def close(): Unit = {
    SocketNetwork.log.info(s"Network ${this} closing...")
    this.thread.interrupt()
    this.handlers.foreach(e =>
      try {
        e.close()
      } catch {
        case x: Exception =>
          log.error(s"Error is occurred while client socket closing! Network ${this}", e)
      }
    )
  }

  private class AcceptorLoop extends Runnable {
    override def run(): Unit = {
      SocketNetwork.log.info(s"Listening of ${SocketNetwork.this.address}:${SocketNetwork.this.port} started...")

      SocketNetwork.this.serverSocket = SocketNetwork.this.serverSocketFactory.createServerSocket(
        SocketNetwork.this.port,
        SocketNetwork.this.backlog,
        SocketNetwork.this.address
      )

      while (!Thread.interrupted()) {
        try {
          val socket = SocketNetwork.this.serverSocket.accept()
          SocketNetwork.log.info(s"Accept connection from ${socket.getRemoteSocketAddress}. Network ${SocketNetwork.this}")

          if (SocketNetwork.this.handlers.size >= SocketNetwork.this.maxConnectionCount) {
            socket.close()
            SocketNetwork.log.info(s"Max connection count reached for network ${SocketNetwork.this}")
          } else {
            val socketOwner = SocketNetwork.this.socketHandler.handle(socket, SocketNetwork.this)
            SocketNetwork.this.handlers.add(socketOwner)
          }

        } catch {
          case e: Exception =>
            SocketNetwork.log.error(s"Error is occurred while socket accepting", e)
        }
      }
    }
  }
}

object SocketNetwork {
  private val log = LoggerFactory.getLogger(classOf[SocketNetwork])

  private val cleaner = Cleaner.create()
}