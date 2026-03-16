package io.github.mathter.memifydb.core.value;

/**
 * Translator java objects to {@linkplain Value}.
 */
public interface ValueTranslator {
    /**
     * Method returns {@linkplain Value} representation of java specified object.
     *
     * @param object java object.
     * @param <T>    type of the java object.
     * @return {@linkplain Value} representation of the java object.
     */
    public <T> Value from(T object);

    /**
     * Method translates value to the specific java instance of type {@code clazz}
     *
     * @param value value.
     * @param clazz targert class.
     * @param <T>   type of class.
     * @return java instance.
     */
    public <T> T to(Value value, Class<T> clazz);
}
