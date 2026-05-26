module io.github.mathter.memifydb.transaction.api {
    requires transitive java.transaction.xa;

    exports io.github.mathter.memifydb.transaction;
    exports io.github.mathter.memifydb.transaction.xa;
}