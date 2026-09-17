package io.github.mathter.memifydb.command.v1

import io.github.mathter.memifydb.command.CommandSerizationFactoryProvider
import org.junit.jupiter.api.{Assertions, Test}

class CommandSerizationFactoryProviderTest {
  @Test
  def test(): Unit = {
    val provider = CommandSerizationFactoryProvider.apply(Const.id)
    Assertions.assertNotNull(provider)

    val factoty = provider.instance(Map(Const.propertyValueSerializationFactory -> Const.defaultValueSerializationFactoryId))
    Assertions.assertNotNull(factoty)

    val factory1 = provider.instance()
    Assertions.assertNotNull(factoty)
  }
}
