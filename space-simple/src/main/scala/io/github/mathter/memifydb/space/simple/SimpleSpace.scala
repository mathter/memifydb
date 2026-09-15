package io.github.mathter.memifydb.space.simple

import io.github.mathter.memifydb.space.{KeyValueOperations, Space}
import io.github.mathter.memifydb.transaction.xa.XaResourceProvider

import java.util.UUID

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
private class SimpleSpace(
                           final val id: UUID,
                           final val name: String,
                           final val transactionTimeout: Int,
                           final val ops: SimpleOperations = new SimpleOperations
                         )
  extends Space[KeyValueOperations] {
  private var isClosed = false

  private val xaRes = new SimpleXaResource(this, this.transactionTimeout)

  override def closed: Boolean = this.isClosed

  override def operations: SimpleOperations = ops

  override def xaResource: XaResourceProvider[KeyValueOperations] =
    if (this.isClosed) {
      throw new IllegalStateException(s"Error while closing Space id=${this.id}, name=${this.name}")
    } else {
      this.xaRes
    }
}
