package io.github.mathter.memifydb.space;

import io.github.mathter.memifydb.core.value.Value;

public class UnsupportedKeyException extends RuntimeException {
    private final Value key;

    public UnsupportedKeyException(Value key) {
        this.key = key;
    }

    @Override
    public String getMessage() {
        return "Unsupported key: " + this.key;
    }

    public Value getKey() {
        return key;
    }
}
