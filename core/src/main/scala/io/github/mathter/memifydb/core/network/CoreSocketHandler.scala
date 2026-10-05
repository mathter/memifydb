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
 * <p>
 */
package io.github.mathter.memifydb.core.network

import io.github.mathter.memifydb.command.SerializationFactory
import io.github.mathter.memifydb.core.command.CommandProcessor
import io.github.mathter.memifydb.core.network.CoreSocketHandler.log
import io.github.mathter.memifydb.network.socket.{Handler, SocketHandler, SocketNetwork}
import org.slf4j.LoggerFactory

import java.net.Socket
import java.util.concurrent.{Executor, Executors}

class CoreSocketHandler(val serializationFactory: SerializationFactory,
                        commandProcessor: CommandProcessor) extends SocketHandler {
  private val executor: Executor = Executors.newVirtualThreadPerTaskExecutor()

  override def handle(socket: Socket, network: SocketNetwork): Handler = {
    log.atInfo()
      .addArgument(() => s"Start handling socket ${socket} of network ${network}")

    val handler = new CoreHandler(
      socket,
      this.serializationFactory.serializer,
      this.serializationFactory.deserializer,
      commandProcessor)
    this.executor.execute(handler)
    handler
  }
}

object CoreSocketHandler {
  private val log = LoggerFactory.getLogger(classOf[CoreSocketHandler])
}
