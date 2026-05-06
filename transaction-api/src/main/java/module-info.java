module io.github.mathter.memifydb.transaction.api {
    requires java.transaction.xa;

    exports io.github.mathter.memifydb.transaction;
    exports io.github.mathter.memifydb.transaction.xa;
}