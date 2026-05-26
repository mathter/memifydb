package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.common.data.Value;
import io.github.mathter.memifydb.common.data.ValueFactory;
import io.github.mathter.memifydb.common.data.ValueTranslator;
import io.github.mathter.memifydb.common.data.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.space.KeyValueOperations;
import io.github.mathter.memifydb.space.SpaceFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

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
public final class LoadTest {
    private static final int THREAD_COUNT = 10;

    private static final int COUNT = 1_000_000;

    private LoadTest() {
    }

    static void main() {
        final Collection<CompletableFuture<?>> completableFutures = new ArrayList<>();

        final ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);
        final ValueTranslator translator = valueFactory.translator();

        final KeyValueOperations space = SpaceFactory.getInstance(Const.ID).get(RandomStringUtils.random(5));

        final List<Value> keys = LongStream.range(0, THREAD_COUNT * COUNT)
                .map(e -> RandomUtils.nextLong())
                .mapToObj(translator::from)
                .toList();

        new Counting(() -> {
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int ii = i;
                completableFutures.add(
                        CompletableFuture.runAsync(
                                new Counting(() -> {
                                    keys.stream()
                                            .skip(ii * COUNT)
                                            .limit(COUNT)
                                            .forEach(key -> {
                                                final Person person = new Person(
                                                        RandomStringUtils.random(10),
                                                        RandomStringUtils.random(10),
                                                        RandomUtils.nextInt()
                                                );
                                                space.put(key, translator.from(person));
                                            });
                                }
                                )
                        )
                );
            }

            for (int i = 0; i < THREAD_COUNT; i++) {
                completableFutures.add(
                        CompletableFuture.runAsync(
                                new Counting(() -> {
                                    keys.stream()
                                            .forEach(key -> {
                                                        space.get(key);
                                                    }
                                            );
                                }
                                )
                        )
                );

                completableFutures.forEach(e -> e.join());
            }
        }).run();

        System.exit(0);
    }

    private static class Counting implements Runnable {
        private final Runnable runnable;

        Counting(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            final long start = System.currentTimeMillis();
            this.runnable.run();
            final long diff = System.currentTimeMillis() - start;
            final String message = Thread.currentThread().getName() + " Total: " + diff + " One: " + ((float) diff / COUNT);
            System.out.println(message);
        }
    }

    private static class Person {
        private String firstName;

        private String lastName;

        private int age;

        Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return this.firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return this.lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
