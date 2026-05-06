package io.github.mathter.memifydb.space;

@FunctionalInterface
public interface OperationsProvider<T extends Operations> {
    public T operatons();
}
