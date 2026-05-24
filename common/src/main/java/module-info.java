module io.github.mathter.memifydb.common {
    requires java.transaction.xa;
    exports io.github.mathter.memifydb.common.xa;
    exports io.github.mathter.memifydb.common.util;
    exports io.github.mathter.memifydb.common.data;

    uses io.github.mathter.memifydb.common.data.ValueFactory;
}