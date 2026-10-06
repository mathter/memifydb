package io.github.mathter.memifydb.space

import java.util.{Properties, ServiceLoader}
import scala.jdk.CollectionConverters.IterableHasAsScala
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
 * SpaceFactory.
 */
trait SpaceFactory {
  def id: String

  def instance[O <: Operations, T <: Space[O]](name: String): T = this.instance(name, null)

  def instance[O <: Operations, T <: Space[O]](name: String, properties: Map[Any, Any]): T
}

object SpaceFactory {
  def apply(id: String): SpaceFactory = {
    val serviceLoader = ServiceLoader.load(classOf[SpaceFactory])

    serviceLoader.asScala.find(e => id == e.id)
      .getOrElse(throw new IllegalStateException(s"There is no SpaceFactory with id='${id}'!"))
  }
}