package io.github.mathter.memifydb.data.cbor.fasterxml

import io.github.mathter.memifydb.common.data.{Value, ValueSerelizationFactory}
import org.junit.jupiter.api.{Assertions, Test}
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
class ValueSerializationFactoryTest {
  @Test
  def test = {
    val serializationFactory = ValueSerelizationFactory(FasterXmlValueSerializationFactory.id)
    val serializer = serializationFactory.serializer
    val deserializer = serializationFactory.deserializer

    val v = serializer.serialize("This is same string")
    Assertions.assertNotNull(v)
    Assertions.assertNotNull(v.raw)

    val r: Value[String] = deserializer.deserialize(v.raw)
    Assertions.assertNotNull(r)
    Assertions.assertEquals(r, v)
    Assertions.assertEquals(v, r)
  }
}
