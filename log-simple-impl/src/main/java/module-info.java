module log.simple.impl {
    requires io.github.mathter.memifydb.core;
    requires io.github.mathter.memifydb.log;

    provides io.github.mathter.memifydb.log.LogFactory with io.github.mathter.memifydb.log.simple.FileLogFactory;
}