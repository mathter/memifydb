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
package io.github.mathter.memifydb.network.socket

import io.github.mathter.memifydb.network.{Network, NetworkFactory}

import java.net.InetAddress

class SocketNetworkFactory extends NetworkFactory {
  override def id: String = Const.id

  override def instance(name: String, properties: Map[Any, Any]): Network = {
    new SocketNetwork(
      this.buildAddress(properties),
      this.buildPort(properties),
      this.buildBacklog(properties),
      this.buildMaxConnectionCount(properties),
      this.buildSocketAcceptor(properties)
    )
  }

  private def buildAddress(properties: Map[Any, Any]): InetAddress = {
    if (properties != null) {
      properties.get(Const.propertyAddress)
        .map {
          case x: String => InetAddress.getByName(x)
          case x: InetAddress => x
          case x => throw new IllegalStateException(s"'${x} invalid ${Const.propertyAddress} format")
        }
        .getOrElse(throw new IllegalStateException(s"There is no ${Const.propertyAddress} to listen"))
    } else {
      throw new IllegalStateException(s"There is no ${Const.propertyAddress} to listen")
    }
  }

  private def buildPort(properties: Map[Any, Any]): Int = {
    if (properties != null) {
      properties.get(Const.propertyPort)
        .map {
          case x: String => x.toInt
          case x: Int => x
          case x => throw new IllegalStateException(s"'${x} invalid ${Const.propertyPort} format")
        }
        .getOrElse(throw new IllegalStateException(s"There is no port to listen"))
    } else {
      throw new IllegalStateException(s"There is no ${Const.propertyPort} to listen")
    }
  }

  private def buildBacklog(properties: Map[Any, Any]): Int = {
    if (properties != null) {
      properties.get(Const.propertyBacklog)
        .map {
          case x: String => x.toInt
          case x: Int => x
          case x => throw new IllegalStateException(s"'${x} invalid ${Const.propertyBacklog} format")
        }
        .getOrElse(Const.defaultBacklog)
    } else {
      Const.defaultBacklog
    }
  }

  private def buildMaxConnectionCount(properties: Map[Any, Any]): Int = {
    if (properties != null) {
      properties.get(Const.propertyMaxConnectionCount)
        .map {
          case x: String => x.toInt
          case x: Int => x
          case x => throw new IllegalStateException(s"'${x} invalid ${Const.propertyMaxConnectionCount} format")
        }
        .getOrElse(Const.defaultMaxConnectionCount)
    } else {
      Const.defaultMaxConnectionCount
    }
  }

  private def buildSocketAcceptor(properties: Map[Any, Any]): SocketHandler = {
    if (properties != null) {
      properties.get(Const.propertySocketHandler)
        .map {
          case x: SocketHandler => x
          case x => throw new IllegalStateException(s"'${x} invalid ${Const.propertySocketHandler} format")
        }
        .getOrElse(new CommandSocketHandler)
    } else {
      new CommandSocketHandler
    }
  }
}
