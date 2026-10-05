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

import io.github.mathter.memifydb.command.v1.{InitCommand, SayHelloCommand}
import io.github.mathter.memifydb.command.{Command, Desc, Sequencable}
import io.github.mathter.memifydb.core.auth.{AuthService, ContextParam}
import io.github.mathter.memifydb.core.command.{CommandProcessor, Context}
import org.slf4j.LoggerFactory

private class NetworkCommandProcessor(val authService: AuthService) extends CommandProcessor {
  override def process(using context: Context, command: Command): Desc & Sequencable = {
    command match {
      case x: SayHelloCommand => this.auth(x)

      case _ => null
    }
  }

  private def auth(command: SayHelloCommand)(using context: Context): Desc & Sequencable = {
    val principal = this.authService.auth(command.login, command.password)

    if (principal != null) {
      NetworkCommandProcessor.log.info(s"User '${principal.getName}' is logged!")
      context.params.put(ContextParam.Principal, principal)

      new InitCommand(command.sequence, "cbor")
    } else {
      context.close()
      null
    }
  }
}

private object NetworkCommandProcessor {
  private val log = LoggerFactory.getLogger(classOf[NetworkCommandProcessor])
}
