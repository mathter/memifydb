package io.github.mathter.memifydb.core.command

import io.github.mathter.memifydb.command.v1.{UnitCommand, VoidResult}
import io.github.mathter.memifydb.command.{Command, Sequence}
import org.apache.commons.lang3.RandomUtils
import org.junit.jupiter.api.{Assertions, Test}
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.{ArgumentMatchers, Mockito}

import scala.collection.mutable

@ExtendWith(Array(classOf[MockitoExtension]))
class CompositeCommandProcessorTest {
  @Test
  def test(): Unit = {
    val commandProcessor0 = Mockito.mock(classOf[CommandProcessor])
    val commandProcessor1 = Mockito.mock(classOf[CommandProcessor])
    val commandProcessor2 = Mockito.mock(classOf[CommandProcessor])
    val commandProcessor = new CompositeCommandProcessor(commandProcessor0, commandProcessor1, commandProcessor2)

    Mockito.when(
      commandProcessor1.process(
        ArgumentMatchers.any(classOf[Context]),
        ArgumentMatchers.any(classOf[Command])
      )
    ).thenReturn(new VoidResult(Sequence(0)))

    val result = commandProcessor.process(
      new Context {
        override def params: mutable.Map[Any, Any] = ???

        override def close(): Unit = ???
      },
      new UnitCommand(Sequence(RandomUtils.insecure().randomLong()))
    )

    Assertions.assertNotNull(result)
    Assertions.assertTrue(result.isInstanceOf[VoidResult])
    Mockito.verify(commandProcessor0, Mockito.times(1)).process(ArgumentMatchers.any(classOf[Context]), ArgumentMatchers.any(classOf[Command]))
    Mockito.verify(commandProcessor1, Mockito.times(1)).process(ArgumentMatchers.any(classOf[Context]), ArgumentMatchers.any(classOf[Command]))
    Mockito.verify(commandProcessor2, Mockito.times(0)).process(ArgumentMatchers.any(classOf[Context]), ArgumentMatchers.any(classOf[Command]))
  }
}
