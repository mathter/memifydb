package io.github.mathter.memifydb.log;

import java.io.IOException;
import java.util.stream.Stream;

public interface Log {
    public void log(Package pack) throws IOException;

    public Stream<Package> get(long fromTimestamp) throws IOException;
}
