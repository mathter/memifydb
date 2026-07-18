package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data
import io.github.mathter.memifydb.common.data.ValueSerelizationFactory
import io.github.mathter.memifydb.data.cbor.fasterxml.FasterXmlValueSerializationFactory.{valueDeserializer, valueSerializer}
import tools.jackson.databind.ObjectMapper
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
class FasterXmlValueSerializationFactory extends ValueSerelizationFactory {
  override def id: String = FasterXmlValueSerializationFactory.id

  override def serializer: data.ValueSerializer = valueSerializer

  override def deserializer: data.ValueDeserializer = valueDeserializer
}

object FasterXmlValueSerializationFactory {
  val id = "cbor.fasterxml"

  private given mapper: ObjectMapper = ObjectMapper()

  private val valueSerializer = new ValueSerializer

  private val valueDeserializer = new ValueDeserializer()
}