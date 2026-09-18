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

object Const {
  val id = classOf[SocketNetworkFactory].getName

  val propertyAddress = "address"

  val propertyPort = "port"

  val propertyBacklog = "backlog"

  val defaultBacklog = 50

  val propertyMaxConnectionCount = "max-connectionc-count"

  val defaultMaxConnectionCount = 1000

  val propertySerializationFactory = "serialization-factory"

  val defaultSerializationFactory = Const.id

  val propertySocketHandler = "socket-handler"
}
