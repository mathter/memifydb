module io.github.mathter.memifydb.log.api {
    requires transitive io.github.mathter.memifydb.common;

    exports io.github.mathter.memifydb.log;

    uses io.github.mathter.memifydb.log.LogFactory;
}