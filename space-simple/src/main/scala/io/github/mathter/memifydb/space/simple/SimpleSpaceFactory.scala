package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.space.{Operations, Space, SpaceFactory}

import java.util.{Objects, UUID}

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
class SimpleSpaceFactory extends SpaceFactory {
  override def id: String = Const.id

  override def instance[O <: Operations, T <: Space[O]](name: String, properties: Map[Any, Any]): T =
    new SimpleSpace(
      this.buildId(properties),
      Objects.requireNonNull(name, "'name' parameter can't be null!"),
      this.buildTransactionTimeout(properties)
    ).asInstanceOf[T]

  private def buildId(properties: Map[Any, Any]): UUID = {
    if (properties != null) {
      properties.get(Const.propertyId)
        .map {
          case x: String => UUID.fromString(x)
          case x: UUID => x
          case x => throw new IllegalStateException(s"${x} not valid UUID")
        }
        .get
    } else {
      UUID.randomUUID()
    }
  }

  private def buildTransactionTimeout(properties: Map[Any, Any]): Int = {
    if (properties != null) {
      properties.get(Const.propertyTimeOut)
        .map {
          case x: String => Integer.parseInt(x)
          case x: Int => x
          case x: Long => x.toInt
          case x => throw new IllegalStateException(s"${x} not valid integer for transaction timeout")
        }
        .get
    } else {
      Const.defaultTimeOut
    }
  }
}
