package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandDesc, Prefix, Sequence}
import io.github.mathter.memifydb.common.data.Value

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
class GetCommand(sequence: Sequence,
                 val spaceName: Value[String],
                 val key: Value[?])
  extends AbstractCommand(sequence) {
}

object GetCommand extends CommandDesc {
  val prefix: Prefix = PrefixV1(Array(0x01, 0x02))
}