package io.github.mathter.memifydb.space.simple;

import io.github.mathter.memifydb.core.data.fasterxml.FasterXmlValueFactory;
import io.github.mathter.memifydb.core.data.Value;
import io.github.mathter.memifydb.core.data.ValueFactory;
import io.github.mathter.memifydb.core.data.ValueTranslator;
import io.github.mathter.memifydb.space.KeyValueSpace;
import io.github.mathter.memifydb.space.SpaceFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

public final class LoadTest {
    private static final int THREAD_COUNT = 10;

    private static final int COUNT = 1_000_000;

    private LoadTest() {
    }

    static void main() {
        final Collection<CompletableFuture> completableFutures = new ArrayList<>();

        final ValueFactory valueFactory = ValueFactory.get(FasterXmlValueFactory.ID);
        final ValueTranslator translator = valueFactory.translator();

        final KeyValueSpace space = SpaceFactory.getInstance(SimpleSpaceFactory.ID).get(RandomStringUtils.random(5));

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
