package io.github.mathter.memifydb.common.util;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
 */
public class ByteArrayTest {
    private static final int COUNT = 100;

    @ParameterizedTest
    @MethodSource
    public <T> void testTest(
            List<T> originList,
            int elementSize,
            Set<T> set,
            Get<T> get,
            Read<T> read) throws IOException {
        final T[] origin = (T[]) originList.toArray();
        final byte[] result = new byte[elementSize * origin.length];

        for (int i = 0; i < origin.length; i++) {
            set.set(result, i * elementSize, origin[i]);
        }

        for (int i = 0; i < origin.length; i++) {
            Assertions.assertEquals(origin[i], get.get(result, elementSize * i));
        }

        final InputStream is = new ByteArrayInputStream(result);
        for (int i = 0; i < origin.length; i++) {
            Assertions.assertEquals(origin[i], read.read(is));
        }
    }

    public static Stream<Arguments> testTest() {
        return Stream.of(
                Arguments.of(
                        build(COUNT, () -> (byte) RandomUtils.nextInt()),
                        Byte.BYTES,
                        (Set<Byte>) ByteArray::setByteRaw,
                        (Get<Byte>) ByteArray::getByteRaw,
                        (Read<Byte>) ByteArray::readByteRaw
                ),
                Arguments.of(
                        build(COUNT, () -> (char) RandomUtils.nextInt()),
                        Character.BYTES,
                        (Set<Character>) ByteArray::setCharRaw,
                        (Get<Character>) ByteArray::getCharRaw,
                        (Read<Character>) ByteArray::readCharRaw
                ),
                Arguments.of(
                        build(COUNT, () -> RandomUtils.nextInt()),
                        Integer.BYTES,
                        (Set<Integer>) ByteArray::setIntRaw,
                        (Get<Integer>) ByteArray::getIntRaw,
                        (Read<Integer>) ByteArray::readIntRaw
                ),
                Arguments.of(
                        build(COUNT, () -> RandomUtils.nextLong()),
                        Long.BYTES,
                        (Set<Long>) ByteArray::setLongRaw,
                        (Get<Long>) ByteArray::getLongRaw,
                        (Read<Long>) ByteArray::readLongRaw
                ),
                Arguments.of(
                        build(COUNT, () -> RandomUtils.nextFloat()),
                        Float.BYTES,
                        (Set<Float>) ByteArray::setFloatRaw,
                        (Get<Float>) ByteArray::getFloatRaw,
                        (Read<Float>) ByteArray::readFloatRaw
                ),
                Arguments.of(
                        build(COUNT, () -> RandomUtils.nextFloat()),
                        Float.BYTES,
                        (Set<Float>) ByteArray::setFloat,
                        (Get<Float>) ByteArray::getFloat,
                        (Read<Float>) ByteArray::readFloat
                ),
                Arguments.of(
                        build(COUNT, () -> RandomUtils.nextDouble()),
                        Double.BYTES,
                        (Set<Double>) ByteArray::setDoubleRaw,
                        (Get<Double>) ByteArray::getDoubleRaw,
                        (Read<Double>) ByteArray::readDoubleRaw
                ),
                Arguments.of(
                        build(COUNT, () -> RandomUtils.nextDouble()),
                        Double.BYTES,
                        (Set<Double>) ByteArray::setDouble,
                        (Get<Double>) ByteArray::getDouble,
                        (Read<Double>) ByteArray::readDouble
                )
        );
    }

    public static <T> List<T> build(int cnt, Supplier<T> supplier) {
        return IntStream.range(0, cnt)
                .mapToObj(e -> supplier.get())
                .toList();
    }

    @FunctionalInterface
    public interface Set<T> {
        public void set(byte[] a, int offset, T e);
    }

    @FunctionalInterface
    public interface Get<T> {
        public T get(byte[] a, int offset);
    }

    @FunctionalInterface
    public interface Read<T> {
        public T read(InputStream is) throws IOException;
    }
}