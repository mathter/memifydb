package io.github.mathter.memifydb.core.value;

/**
 * Value serializer.
 */
public interface ValueSerializer {
    /**
     * Method returns raw byte representaion of the {@linkplain Value}.
     *
     * @param value value instance.
     * @return byte representaion.
     */
    public byte[] serialize(Value value);
}
