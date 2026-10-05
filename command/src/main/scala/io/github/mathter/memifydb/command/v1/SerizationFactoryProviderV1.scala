package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{Deserializer, Serializer, SerializationFactory, SerizationFactoryProvider}
import io.github.mathter.memifydb.common.data.ValueSerializationFactory

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
class SerizationFactoryProviderV1 extends SerizationFactoryProvider {
  override def id: String = Const.id

  override def instance(properties: Map[Any, Any]): SerializationFactory = {
    new SerializationFactoryV1Impl(this.buildValueSerializationFactory(properties))
  }

  private def buildValueSerializationFactory(properties: Map[Any, Any]): ValueSerializationFactory = {

     if (properties != null) {
       properties.get(Const.propertyValueSerializationFactory)
         .map {
           case x: String => ValueSerializationFactory.apply(x)
           case x: ValueSerializationFactory => x
           case x => throw new IllegalStateException(s"${x} is not valid ValueSerializationFactory!")
         }
         .getOrElse(ValueSerializationFactory.apply(Const.defaultValueSerializationFactoryId))
     } else {
       ValueSerializationFactory.apply(Const.defaultValueSerializationFactoryId)
     }
   }

}

private class SerializationFactoryV1Impl(val valueSerializationFactory: ValueSerializationFactory) extends SerializationFactoryV1 {
  override def serializer: Serializer = new SerializerV1

  override def deserializer: Deserializer = new DeserializerV1(this.valueSerializationFactory.deserializer)
}