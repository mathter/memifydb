package io.github.mathter.memifydb.command

import java.util.ServiceLoader
import scala.jdk.CollectionConverters.given

trait CommandSerizationFactoryProvider {
  def id: String

  def instance(): CommandSerizationFactory = this.instance(null)

  def instance(properties: Map[Any, Any]): CommandSerizationFactory
}


object CommandSerizationFactoryProvider {
  def apply(id: String): CommandSerizationFactoryProvider = {
    val serviceLoader = ServiceLoader.load(classOf[CommandSerizationFactoryProvider])

    serviceLoader.asScala.find(e => id == e.id)
      .getOrElse(throw new IllegalStateException(s"There is no io.github.mathter.memifydb.command.CommandSerizationFactoryProvider with id='${id}'!"))
  }
}