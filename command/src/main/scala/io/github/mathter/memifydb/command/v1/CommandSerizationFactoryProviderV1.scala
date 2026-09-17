package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.{CommandDeserializer, CommandSerializer, CommandSerizationFactory, CommandSerizationFactoryProvider}
import io.github.mathter.memifydb.common.data.ValueSerelizationFactory

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
class CommandSerizationFactoryProviderV1 extends CommandSerizationFactoryProvider {
  override def id: String = Const.id

  override def instance(properties: Map[Any, Any]): CommandSerizationFactory = {
    new CommandSerizationFactoryV1Impl(this.buildValueSerelizationFactory(properties))
  }

  private def buildValueSerelizationFactory(properties: Map[Any, Any]): ValueSerelizationFactory = {
    if (properties != null) {
      properties.get(Const.propertyValueSerializationFactory)
        .map {
          case x: String => ValueSerelizationFactory.apply(x)
          case x: ValueSerelizationFactory => x
          case x => throw new IllegalStateException(s"${x} is not valid ValueSerelizationFactory!")
        }
        .get
    } else {
      ValueSerelizationFactory.apply(Const.defaultValueSerializationFactoryId)
    }
  }
}

private class CommandSerizationFactoryV1Impl(val valueSerelizationFactory: ValueSerelizationFactory) extends CommandSerizationFactoryV1 {
  override def serializer: CommandSerializer = new CommandSerializerV1

  override def deserializer: CommandDeserializer = new CommandDeserializerV1(this.valueSerelizationFactory.deserializer)
}