package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.util.IOUtil
import io.github.mathter.memifydb.command.{Command, Desc, Result, Sequencable, Serializer}
import io.github.mathter.memifydb.common.util.ByteArray

import java.io.OutputStream
import java.nio.charset.StandardCharsets

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
private class SerializerV1 extends Serializer {
  override def serialize(os: OutputStream, command: Desc & Sequencable): Boolean = {
    given _os: OutputStream = os

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

      case x: UnitCommand => {
        true
      }

      case x: InitCommand => {
        IOUtil.write(x.baseSerializationProtocal.getBytes(StandardCharsets.UTF_8))
        true
      }

      case x: SayHelloCommand => {
        IOUtil.write(x.login.getBytes(StandardCharsets.UTF_8))
        IOUtil.write(x.password)
        true
      }

      case x: SpaceInfoCommand => {
        IOUtil.write(x.serializationProtocol.getBytes(StandardCharsets.UTF_8))
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

      case x: XaRecoverTransactionCommand => {
        ByteArray.writeIntRaw(os, x.flags)
        true
      }

      case x: XaRollbackTransactionCommand => {
        IOUtil.write(x.xid)
        true
      }

      case x: XaStartTransactionCommand => {
        IOUtil.write(x.xid)
        ByteArray.writeIntRaw(os, x.flags)
        true
      }

      case x: XaContainerCommand => {
        IOUtil.write(x.xid)

        if (x.commands == null || x.commands.isEmpty) {
          ByteArray.writeIntRaw(os, 0)
        } else {
          ByteArray.writeIntRaw(os, x.commands.size)
          x.commands.foreach(e => this.serialize(os, e))
        }
        true
      }

      case x: VoidResult => {
        true
      }

      case x: ValueResult[?] => {
        IOUtil.write(x.value)
        true
      }

      case x: IllegalCommandResult => {
        IOUtil.write(x.data)
        true
      }

      case _ => false
    }
  }
}
