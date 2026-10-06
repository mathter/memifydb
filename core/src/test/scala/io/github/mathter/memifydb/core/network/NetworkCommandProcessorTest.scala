package io.github.mathter.memifydb.core.network

import io.github.mathter.memifydb.command.Sequence
import io.github.mathter.memifydb.command.v1.{InitCommand, SayHelloCommand}
import io.github.mathter.memifydb.core.auth.AuthService
import io.github.mathter.memifydb.core.command.Context
import org.apache.commons.lang3.{RandomStringUtils, RandomUtils}
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.{Assertions, BeforeEach, Test}
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.stubbing.Answer
import org.mockito.{ArgumentMatchers, InjectMocks, Mock, Mockito}

import java.security.Principal
import scala.collection.mutable
import scala.compiletime.uninitialized

@ExtendWith(Array(classOf[MockitoExtension]))
class NetworkCommandProcessorTest {

  @Mock
  var authService: AuthService = uninitialized

  @InjectMocks
  var networkCommandProcessor: NetworkCommandProcessor = uninitialized

  @Mock
  var context: Context = uninitialized

  @Test
  def testSuccess(): Unit = {
    val sequence = Sequence(RandomUtils.insecure().randomLong())
    val sayHelloCommand = new SayHelloCommand(
      sequence,
      RandomStringUtils.insecure().nextAlphabetic(10),
      RandomUtils.insecure().randomBytes(10)
    )

    Mockito.when(
      authService.auth(
        ArgumentMatchers.any(classOf[String]),
        ArgumentMatchers.any(classOf[Array[Byte]])
      )
    ).thenAnswer((invocation: InvocationOnMock) => {
      new Principal {
        override def getName: String = invocation.getArgument(0)
      }
    })
    Mockito.when(this.context.params)
      .thenReturn(mutable.Map.empty)

    val result = this.networkCommandProcessor.process(using this.context, sayHelloCommand)
    Assertions.assertNotNull(result)
    Assertions.assertTrue(result.isInstanceOf[InitCommand])
    Assertions.assertEquals(sequence, result.asInstanceOf[InitCommand].sequence)
  }

  @Test
  def testFail(): Unit = {
    val sequence = Sequence(RandomUtils.insecure().randomLong())
    val sayHelloCommand = new SayHelloCommand(
      sequence,
      "failed",
      RandomUtils.insecure().randomBytes(10)
    )

    Mockito.when(
      authService.auth(
        ArgumentMatchers.eq("failed"),
        ArgumentMatchers.any(classOf[Array[Byte]])
      )
    ).thenReturn(null)

    val result = this.networkCommandProcessor.process(using this.context, sayHelloCommand)
    Assertions.assertNull(result)
    Mockito.verify(this.context, Mockito.times(1)).close()
  }
}
