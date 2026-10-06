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
 * WITHOUT WARRANTIES OR <b>CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 */
package io.github.mathter.memifydb.core.network

import io.github.mathter.memifydb.command.v1.IllegalCommandResult
import io.github.mathter.memifydb.command.{Deserializer, IllegalPrefixException, Sequence, Serializer}
import io.github.mathter.memifydb.core.command.{CommandProcessor, Context}
import io.github.mathter.memifydb.network.socket.Handler
import org.slf4j.LoggerFactory

import java.io.EOFException
import java.net.Socket
import scala.collection.mutable

class CoreHandler(val socket: Socket,
                  val serizlizer: Serializer,
                  val deserializer: Deserializer,
                  val commandProcessor: CommandProcessor,
                  val params: mutable.Map[Any, Any] = mutable.Map.empty,
                  var continue: Boolean = true
                 ) extends Handler
  , Runnable, Context, AutoCloseable {
  private val is = this.socket.getInputStream

  private val os = this.socket.getOutputStream

  override def run(): Unit = {
    CoreHandler.log.info(s"Start processing cycle for ${this}")

    while (this.continue) {
      try {
        val command = this.deserializer.deserialize(this.is)
        val result = this.commandProcessor.process(this, command)
        this.serizlizer.serialize(this.os, result)
      } catch {
        case e: IllegalPrefixException => {
          CoreHandler.log.error(s"Illegal command prefix ", e)
          val rst = new IllegalCommandResult(Sequence(0), e.prefix.toByteArray)
          this.serizlizer.serialize(this.os, rst)
        }
        case e: EOFException => {
          this.close()
        }
        case e: Exception => {
          CoreHandler.log.error("Command processing error!", e)
        }
      }
    }

    CoreHandler.log.info(s"Stop prcocessing cycle for ${this}")
  }

  override def close(): Unit = {
    this.continue = false

    try {
      this.is.close()
    } catch {
      case e: Exception => CoreHandler.log.error(s"Error is occurred while input stream closing for ${this}!", e)
    }

    try {
      this.os.flush()
      this.os.close()
    } catch {
      case e: Exception => CoreHandler.log.error("Error is occurred while output stream closing for ${this}!", e)
    }

    try {
      this.socket.close()
    } catch {
      case e: Exception => CoreHandler.log.error("Error is occurred while socket closing for ${this}!", e)
    }
  }

  override def toString: String = s"CoreHandler[socket=${this.socket}]"
}

object CoreHandler {
  private val log = LoggerFactory.getLogger(classOf[CoreHandler])
}
