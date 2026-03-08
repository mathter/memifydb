package io.github.mathter.memifydb.core.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Opt is analog of the {@linkplain java.util.Optional} but contains two stats: value (including null) and empty.
 *
 * @param <T> - type of the value.
 */
public abstract sealed class Opt<T> permits Opt.Value, Opt.Empty {

    private static final Opt<?> EMPTY = new Empty<>();

    /**
     * The method returns value based {@linkplain Opt}.
     *
     * @param value value of the {@linkplain Opt} to be created.
     * @param <T>   type of the value.
     * @return {@linkplain Opt} instance.
     */
    public static <T> Opt<T> of(T value) {
        return new Value<T>(value);
    }

    /**
     * The method returns enmpty {@linkplain Opt}.
     *
     * @param <T> type of the value.
     * @return {@linkplain Opt} empty instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> Opt<T> empty() {
        return (Opt<T>) EMPTY;
    }

    /**
     * If a value is present, returns the value, otherwise throws NoSuchElementException.
     *
     * @return the value (including null based value) described by this {@linkplain Opt}.
     * @throws NoSuchElementException if no value is present
     */
    public abstract T get() throws NoSuchElementException;

    /**
     * If a value is present, returns the value, otherwise returns {@code other}.
     *
     * @param other the value to be returned, if no value is present. May be {@code null}.
     * @return the value, if present, otherwise {@code other}.
     */
    public abstract T orElse(T other);

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the supplying function
     */
    public abstract T orElseOrGet(Supplier<? extends T> supplier) throws NullPointerException;

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Optional}
     * @throws NoSuchElementException if no value is present
     */
    public abstract T orElseThrow() throws NoSuchElementException;

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *                          exception to be thrown
     * @return the value, if present
     * @throws X                    if no value is present
     * @throws NullPointerException if no value is present and the exception
     *                              supplying function is {@code null} or produces a {@code null} result
     * @apiNote A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     */
    public abstract <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X, NullPointerException;

    public abstract <U> Opt<U> map(Function<? super T, ? extends U> mapper) throws NullPointerException;

    public abstract boolean isEmpty();

    public abstract boolean isPresent();

    public abstract <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper);

    public abstract void ifPresent(Consumer<? super T> action);

    public abstract void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

    public abstract Opt<T> filter(Predicate<? super T> predicate);

    private static final class Empty<T> extends Opt<T> {
        @Override
        public T get() throws NoSuchElementException {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public T orElseOrGet(Supplier<? extends T> supplier) throws NullPointerException {
            return supplier.get();
        }

        @Override
        public T orElseThrow() throws NoSuchElementException {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X, NullPointerException {
            throw exceptionSupplier.get();
        }

        @Override
        public <U> Opt<U> map(Function<? super T, ? extends U> mapper) throws NullPointerException {
            return (Opt<U>) EMPTY;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) {
            return (Opt<U>) EMPTY;
        }

        @Override
        public void ifPresent(Consumer<? super T> action) {
            // Do nothing.
        }

        @Override
        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            emptyAction.run();
        }

        @Override
        public Opt<T> filter(Predicate<? super T> predicate) {
            return this;
        }

        @Override
        public String toString() {
            return "Opt.empty";
        }
    }

    private static final class Value<T> extends Opt<T> {
        private final T value;

        private Value(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return this.value;
        }

        @Override
        public T orElse(T other) {
            return this.value;
        }

        @Override
        public T orElseOrGet(Supplier<? extends T> supplier) {
            return this.value;
        }

        @Override
        public T orElseThrow() {
            return this.value;
        }

        @Override
        public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X, NullPointerException {
            Objects.requireNonNull(exceptionSupplier);
            return this.value;
        }

        @Override
        public <U> Opt<U> map(Function<? super T, ? extends U> mapper) {
            return Opt.of(mapper.apply(this.value));
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) throws NullPointerException {
            Objects.requireNonNull(mapper);
            return (Opt<U>) mapper.apply(this.value);
        }

        @Override
        public void ifPresent(Consumer<? super T> action) {
            action.accept(this.value);
        }

        @Override
        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            action.accept(this.value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Opt<T> filter(Predicate<? super T> predicate) {
            return predicate.test(this.value) ? this : (Opt<T>) EMPTY;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.value);
        }

        @Override
        public boolean equals(Object obj) {
            final boolean result;

            if (this == obj) {
                result = true;
            } else {
                result = obj instanceof Opt.Value<?> other && Objects.equals(this.value, other.value);
            }

            return result;
        }

        @Override
        public String toString() {
            return "Opt[" + this.value + "]";
        }
    }
}
