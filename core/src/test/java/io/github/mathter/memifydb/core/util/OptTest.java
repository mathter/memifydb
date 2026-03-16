package io.github.mathter.memifydb.core.util;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class OptTest {
    private AutoCloseable closeable;

    @Mock
    private Consumer<String> consumer;

    @Mock
    private Runnable emptyAction;

    @BeforeEach
    public void init() {
        this.closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void destroy() throws Exception {
        this.closeable.close();
    }

    @ParameterizedTest
    @MethodSource
    public void testOf(String value, String other, Integer mapped) {
        final Opt<String> opt = Opt.of(value);

        Assertions.assertEquals(value, opt.get());
        Assertions.assertEquals(value, opt.orElse(other));
        Assertions.assertEquals(value, opt.orElseOrGet(() -> other));
        Assertions.assertEquals(value, opt.orElseThrow());
        Assertions.assertEquals(value, opt.orElseThrow(RuntimeException::new));
        Assertions.assertEquals(opt, opt.filter(e -> true));
        Assertions.assertEquals(Opt.empty(), opt.filter(e -> false));
        Assertions.assertEquals(Opt.of(mapped), opt.map(e -> mapped));
        Assertions.assertEquals(Opt.of(mapped), opt.flatMap(e -> Opt.of(mapped)));
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertFalse(opt.isEmpty());

        opt.ifPresent(this.consumer);
        Mockito.verify(this.consumer, Mockito.times(1)).accept(Mockito.any());

        opt.ifPresentOrElse(this.consumer, this.emptyAction);
        Mockito.verify(this.consumer, Mockito.times(2)).accept(Mockito.any());
        Mockito.verify(this.emptyAction, Mockito.times(0)).run();

        final Opt<String> opt1 = Opt.of(value);
        Assertions.assertEquals(opt, opt1);
    }

    public static Stream<Arguments> testOf() {
        return Stream.of(
                Arguments.of("value", "other", RandomUtils.nextInt()),
                Arguments.of(null, "other", RandomUtils.nextInt())
        );
    }

    @Test
    public void testEmpty() {
        final String other = "Hello";
        final Opt<String> opt = Opt.empty();

        Assertions.assertThrows(NoSuchElementException.class, opt::get);
        Assertions.assertEquals(other, opt.orElse(other));
        Assertions.assertEquals(other, opt.orElseOrGet(() -> other));
        Assertions.assertThrows(NoSuchElementException.class, opt::orElseThrow);
        Assertions.assertThrows(RuntimeException.class, () -> opt.orElseThrow(RuntimeException::new));
        Assertions.assertEquals(Opt.empty(), opt.filter(e -> true));
        Assertions.assertEquals(Opt.empty(), opt.filter(e -> false));
        Assertions.assertEquals(Opt.empty(), opt.map(e -> new Object()));
        Assertions.assertEquals(Opt.empty(), opt.flatMap(e -> Opt.of(new Object())));
        Assertions.assertFalse(opt.isPresent());
        Assertions.assertTrue(opt.isEmpty());

        opt.ifPresent(this.consumer);
        Mockito.verify(this.consumer, Mockito.times(0)).accept(Mockito.any());

        opt.ifPresentOrElse(this.consumer, this.emptyAction);
        Mockito.verify(this.consumer, Mockito.times(0)).accept(Mockito.any());
        Mockito.verify(this.emptyAction, Mockito.times(1)).run();
    }
}
