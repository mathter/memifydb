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
package io.github.mathter.memifydb.network.socket

import io.github.mathter.memifydb.network.socket.EchoSocketHandler.log
import org.slf4j.LoggerFactory

import java.io.{InputStream, OutputStream}
import java.net.Socket
import java.util.logging.{Level, Logger}

class EchoSocketHandler extends SocketHandler {
  override def handle(socket: Socket, network: SocketNetwork): Handler = {
    log.info(s"Handle socket ${socket} and network ${network}")

    val handler = new EchoHandler(socket.getInputStream, socket.getOutputStream, socket)
    handler.start()

    handler
  }

  class EchoHandler(val is: InputStream, val os: OutputStream, val socket: Socket) extends Thread, Handler {
    override def run(): Unit = {
      val buf = new Array[Byte](1024)

      while (!Thread.interrupted()) {
        val count = this.is.read(buf)
        if (count < 0) {
          return
        } else if (count > 0) {
          log.atTrace()
            .addArgument((0 until count).map(e => "%02X" format e).foldLeft("0x")((left, right) => left + right))
            .log("{}")
          this.os.write(buf, 0, count)
        }
      }
    }

    override def close(): Unit = {
      EchoSocketHandler.log.info(s"Release handler this socket ${socket}")
      this.interrupt()

      try {
        this.is.close()
      } catch {
        case e: Exception =>
          EchoSocketHandler.log.error(s"Error is occurred while socket closing!", e)
      }

      try {
        this.os.flush()
        this.os.close()
      } catch {
        case e: Exception =>
          EchoSocketHandler.log.error(s"Error is occurred while socket closing!", e)
      }

      try {
        socket.close()
      } catch {
        case e: Exception =>
          EchoSocketHandler.log.error(s"Error is occurred while socket closing!", e)
      }
    }
  }
}

object EchoSocketHandler {
  private val log = LoggerFactory.getLogger(classOf[EchoSocketHandler])
}
