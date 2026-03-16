package io.github.mathter.memifydb.core.value;

/**
 * This class is universal representation of any data in raw format.
 */
public interface Value {
    /**
     * The method returns the underlying data in a specific Java format.
     * The return value is determined by the format of the underlying data and there is no automatic conversion to type {@code T}.
     *
     * @param <T> type of java.
     * @return object.
     */
    public <T> T get();
}
