package io.github.mathter.memifydb.common.data

import java.util.ServiceLoader
import scala.jdk.CollectionConverters.given

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
 * The class is factory of instances of the serilizer, deseralizer any type objects
 * to and from a byte array, as well as java objects
 */
abstract class ValueSerelizationFactory {
  /**
   *
   * @return
   */
  def id: String

  def serializer: ValueSerializer

  def deserializer: ValueDeserializer
}

object ValueSerelizationFactory {
  def apply(id: String): ValueSerelizationFactory = {
    val serviceLoader = ServiceLoader.load(classOf[ValueSerelizationFactory])

    serviceLoader.asScala.find(e => id == e.id)
      .getOrElse(
        throw new IllegalStateException(s"There is no ValueSerelizationFactory with id='${id}'")
      )
  }
}