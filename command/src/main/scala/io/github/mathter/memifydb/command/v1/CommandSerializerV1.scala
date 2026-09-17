package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.util.IOUtil
import io.github.mathter.memifydb.command.{Command, CommandSerializer}
import io.github.mathter.memifydb.common.util.ByteArray

import java.io.OutputStream
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
private class CommandSerializerV1 extends CommandSerializer {
  override def serialize(using os: OutputStream, command: Command): Boolean = {
    os.write(command.prefix.toByteArray)
    IOUtil.write(command.sequence)

    command match {
      case x: GetCommand => {
        IOUtil.write(x.spaceName)
        IOUtil.write(x.key)

        true
      }

      case x: PutCommand => {
        IOUtil.write(x.spaceName)
        IOUtil.write(x.key)
        IOUtil.write(x.value)

        true
      }

      case x: XaCommitTransactionCommand => {
        IOUtil.write(x.xid)
        IOUtil.write(x.onePhase)

        true
      }

      case x: XaEndTransactionCommand => {
        IOUtil.write(x.xid)
        ByteArray.writeIntRaw(os, x.flags)

        true
      }

      case x: XaPrepareTransactionCommand => {
        IOUtil.write(x.xid)

        true
      }

      case _ => false
    }
  }
}
