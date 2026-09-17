package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Command, CommandDesc, Prefix, Sequence}

abstract class AbstractCommand(val sequence: Sequence) extends Command, CommandDesc {
  private final val commandDesc = AbstractCommand.o(this.getClass)

  override def prefix: Prefix = this.commandDesc.prefix
}

private object AbstractCommand {
  def o[T](clazz: Class[T]): CommandDesc = {
    Class.forName(clazz.getName + "$")
      .getField("MODULE$")
      .get(null)
      .asInstanceOf[CommandDesc]
  }
}