package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.util.IOUtil
import io.github.mathter.memifydb.command.{Command, CommandDeserializer}
import io.github.mathter.memifydb.common.data.ValueDeserializer
import io.github.mathter.memifydb.common.util.ByteArray

import java.io.InputStream
import scala.reflect.ClassTag

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
private class CommandDeserializerV1(private val valueDeserializer: ValueDeserializer) extends CommandDeserializer {
  override def deserialize[T <: Command](using is: InputStream): T = {
    val prefix = PrefixV1(is.readNBytes(2))

    (prefix match {
      case GetCommand.prefix => {
        implicit val classTag: ClassTag[Nothing] = ClassTag(classOf[Object])

        new GetCommand(
          IOUtil.readSequence,
          this.valueDeserializer.deserialize(IOUtil.readValueRaw),
          this.valueDeserializer.deserialize(IOUtil.readValueRaw)
        )
      }

      case PutCommand.prefix => {
        implicit val classTag: ClassTag[Nothing] = ClassTag(classOf[Object])

        new PutCommand(
          IOUtil.readSequence,
          this.valueDeserializer.deserialize(IOUtil.readValueRaw),
          this.valueDeserializer.deserialize(IOUtil.readValueRaw),
          this.valueDeserializer.deserialize(IOUtil.readValueRaw)
        )
      }

      case XaCommitTransactionCommand.prefix => {
        new XaCommitTransactionCommand(
          IOUtil.readSequence,
          IOUtil.readXid,
          IOUtil.readBoolean
        )
      }

      case XaEndTransactionCommand.prefix => {
        new XaEndTransactionCommand(
          IOUtil.readSequence,
          IOUtil.readXid,
          ByteArray.readIntRaw(is)
        )
      }

      case XaPrepareTransactionCommand.prefix => {
        new XaPrepareTransactionCommand(
          IOUtil.readSequence,
          IOUtil.readXid
        )
      }

      case _ => {
        throw new IllegalStateException(s"${prefix} is unknown prefix!")
      }
    })
      .asInstanceOf[T]
  }
}
