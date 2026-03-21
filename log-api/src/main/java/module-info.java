module io.github.mathter.memifydb.log {
    requires transitive io.github.mathter.memifydb.core;

    exports io.github.mathter.memifydb.log;

    uses io.github.mathter.memifydb.log.LogFactory;
}