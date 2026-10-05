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
package io.github.mathter.memifydb.core.command

import io.github.mathter.memifydb.command.{Command, Desc, Result, Sequencable}

import scala.util.boundary
import scala.util.boundary.break

class CompositeCommandProcessor(private val commandProcessors: CommandProcessor*) extends CommandProcessor {
  override def process(context: Context, command: Command): Desc & Sequencable = {
    if (this.commandProcessors != null && this.commandProcessors.nonEmpty) {
      boundary:
        for (processor <- this.commandProcessors) {
          val result = processor.process(context, command)
          if result != null then break(result)
        }
        null
    } else {
      null
    }
  }
}
