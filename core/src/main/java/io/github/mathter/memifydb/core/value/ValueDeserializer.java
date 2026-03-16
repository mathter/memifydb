package io.github.mathter.memifydb.core.value;

/**
 * Value deserializer.
 */
public interface ValueDeserializer {
    /**
     * Method returns {@linkplain Value}.
     *
     * @param bytes raw representaion of the value.
     * @return value instance.
     */
    public Value deserialize(byte[] bytes);
}
