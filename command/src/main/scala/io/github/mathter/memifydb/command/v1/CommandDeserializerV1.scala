package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.util.IOUtil
import io.github.mathter.memifydb.command.{Command, CommandDeserializer}
import io.github.mathter.memifydb.common.data.ValueDeserializer

import java.io.InputStream
import scala.reflect.ClassTag

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

      case _ => {
        throw new IllegalStateException(s"${prefix} is unknown prefix!")
      }
    })
      .asInstanceOf[T]
  }
}
